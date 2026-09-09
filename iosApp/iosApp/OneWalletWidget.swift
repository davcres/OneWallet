import WidgetKit
import SwiftUI
import OneWalletShared

struct PortfolioWidgetEntry: TimelineEntry {
    let date: Date
    let balance: String
    let currency: String
    let items: [WidgetAssetItem]
}

struct PortfolioTimelineProvider: TimelineProvider {
    func placeholder(in context: Context) -> PortfolioWidgetEntry {
        PortfolioWidgetEntry(
            date: Date(),
            balance: "€ 12,450.00",
            currency: "€",
            items: [
                WidgetAssetItem(
                    name: "Bitcoin",
                    symbol: "BTC",
                    quantity: 0.5,
                    value: 30000.0,
                    formattedValue: "€ 30,000",
                    trendPercent: 2.5,
                    isPositive: true
                )
            ]
        )
    }

    func getSnapshot(in context: Context, completion: @escaping (PortfolioWidgetEntry) -> Void) {
        completion(placeholder(in: context))
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<PortfolioWidgetEntry>) -> Void) {
        let currentDate = Date()
        let refreshDate = Calendar.current.date(byAdding: .minute, value: 30, to: currentDate) ?? currentDate

        Task {
            let provider = WidgetDataProvider()
            do {
                let snapshot = try await provider.getSnapshot()
                let entry = PortfolioWidgetEntry(
                    date: currentDate,
                    balance: snapshot.formattedBalance,
                    currency: snapshot.currencySymbol,
                    items: snapshot.items
                )
                let timeline = Timeline(entries: [entry], policy: .after(refreshDate))
                completion(timeline)
            } catch {
                let fallback = placeholder(in: context)
                let timeline = Timeline(entries: [fallback], policy: .after(refreshDate))
                completion(timeline)
            }
        }
    }
}

struct OneWalletWidgetEntryView: View {
    var entry: PortfolioWidgetEntry
    @Environment(\.widgetFamily) var family

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text("OneWallet")
                    .font(.caption2)
                    .fontWeight(.bold)
                    .foregroundColor(.secondary)
                Spacer()
                Image(systemName: "wallet.pass.fill")
                    .font(.caption2)
                    .foregroundColor(.blue)
            }

            Text("Total Balance")
                .font(.caption)
                .foregroundColor(.secondary)

            Text(entry.balance)
                .font(.title2)
                .fontWeight(.bold)
                .foregroundColor(.primary)

            if family != .systemSmall && !entry.items.isEmpty {
                Divider()
                ForEach(entry.items.prefix(3), id: \.symbol) { item in
                    HStack {
                        Text(item.symbol)
                            .font(.caption)
                            .fontWeight(.medium)
                        Spacer()
                        Text(item.formattedValue)
                            .font(.caption)
                        Text(String(format: "%+.1f%%", item.trendPercent))
                            .font(.caption2)
                            .foregroundColor(item.isPositive ? .green : .red)
                    }
                }
            }
        }
        .padding()
    }
}

struct OneWalletWidget: Widget {
    let kind: String = "OneWalletWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: PortfolioTimelineProvider()) { entry in
            if #available(iOS 17.0, *) {
                OneWalletWidgetEntryView(entry: entry)
                    .containerBackground(for: .widget) {
                        Color(.systemBackground)
                    }
            } else {
                OneWalletWidgetEntryView(entry: entry)
                    .background(Color(.systemBackground))
            }
        }
        .configurationDisplayName("OneWallet Portfolio")
        .description("Track your portfolio balance and assets.")
        .supportedFamilies([.systemSmall, .systemMedium])
    }
}
