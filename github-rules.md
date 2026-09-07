# GitHub & Git Guidelines for Senior Android Engineers

This document defines the Git and GitHub workflow standards for the **OneWallet** project. Every agent and developer must strictly adhere to these rules when managing branches, commits, quality gates, and Pull Requests.

---

## 1. Branching Strategy

- **Base Branch:** All feature work, bug fixes, refactoring, and maintenance branches must be cut from and target `develop` as the base branch (`main` is reserved for production releases).
- **Branch Naming Conventions:**
  Always use kebab-case with one of the following prefixes:
  - `feature/<kebab-case-description>`: New functionality, UI screens, or business logic (e.g. `feature/pause-global-market-card`).
  - `bugfix/<kebab-case-description>`: Bug and defect fixes (e.g. `bugfix/status-bar-icons-theme`).
  - `chore/<kebab-case-description>`: Tooling, dependency upgrades, dead code cleanup, or resource reorganization (e.g. `chore/remove-duplicate-resources`).
  - `refactor/<kebab-case-description>`: Code improvements that neither fix a bug nor add a feature (e.g. `refactor/modularize-koin-di`).
  - `arch/<kebab-case-description>`: Large architectural restructuring (e.g. `arch/feature-by-layer-modularization`).

---

## 2. Commit Standards (Conventional Commits)

Commits must be atomic, self-contained, and follow the project's conventional format:

```
<type>(<scope>): <imperative short description>
```

- **`<type>`:** `feature`, `bugfix`, `chore`, `refactor`, `test`, `arch`.
- **`<scope>`:** The module or component affected (e.g. `res`, `core`, `portfolio`, `market`, `theme`, `widget`, `data`).
- **Examples:**
  - `chore(res): remove duplicate resources from app module`
  - `feature(pause card): pause global market countdown on touch`
  - `bugfix(theme): update status bar icon color dynamically based on theme`
  - `refactor(arch): migrate to feature-by-layer multi-module architecture`

---

## 3. Mandatory Pre-Flight Quality Gates

**NEVER** push code or create a Pull Request without running and verifying the following commands locally:

1. **Compilation Check:**
   ```bash
   ./gradlew assembleDebug
   ```
2. **Unit Tests:**
   ```bash
   ./gradlew testDebugUnitTest
   ```
3. **Static Code Analysis (Linter):**
   ```bash
   ./gradlew detekt
   ```

All three checks must pass with `BUILD SUCCESSFUL` and 0 errors before opening a PR.

---

## 4. Pull Request Protocol (via GitHub CLI `gh`)

When the user requests to create a Pull Request:

### Step 1: Ensure Local Working Branch
- Verify git status: `git status`
- Create and switch to the appropriate branch:
  ```bash
  git checkout -b <branch-name>
  ```

### Step 2: Stage & Commit
- Stage modified files intentionally (never commit build artifacts, `.DS_Store`, or untracked local caches).
- Commit with conventional message:
  ```bash
  git commit -m "<type>(<scope>): <description>"
  ```

### Step 3: Push to Origin
- Push branch setting upstream:
  ```bash
  git push -u origin <branch-name>
  ```

### Step 4: Create Pull Request via `gh`
- Use `gh pr create` targeting `--base develop`.
- The PR title must mirror the conventional commit message.
- The PR body must follow this markdown template:

```markdown
### 📝 Summary
Brief 2-3 sentence overview of what was implemented or resolved and why.

### 🛠️ Changes
- **`:module_name`**:
  - Detailed bullet point of change.
  - Detailed bullet point of change.

### 💡 Motivation
Explanation of the problem solved, architectural rationale, or user experience benefit.

### 🧪 Verification
- [x] Compilation: `./gradlew assembleDebug`
- [x] Unit Tests: `./gradlew testDebugUnitTest`
- [x] Static Analysis: `./gradlew detekt`
- [x] Manual Verification (describe specific checks performed)
```

### Step 5: Deliver PR Link
- Provide the generated GitHub Pull Request URL directly to the user so they can review it immediately.
