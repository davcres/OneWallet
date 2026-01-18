# OneWallet

## Configuración de secretos (API keys)

Este proyecto utiliza claves privadas (API keys) que **no deben versionarse** en el repositorio.
Para trabajar en local y en CI/CD usamos dos mecanismos:

- En **local**: fichero `secrets.properties` (no se sube a git).
- En **CI/CD**: variables de entorno configuradas como *secrets* en el pipeline.

---

### 1. Fichero `secrets.properties` (entorno local)


1. En la **raíz del proyecto** (misma carpeta que `settings.gradle`), crea un archivo llamado:

    ```text
   secrets.properties
    ```

2. Añade dentro las claves necesarias. Ejemplo:

    ```text
   MY_SECRET_API_KEY = pon_aquí_tu_clave_real
    ```

3. Asegúrate de que no se versiona. El archivo secrets.properties está añadido a .gitignore.
Si no lo estuviera, añade esta línea al .gitignore:

    ```text
   /secrets.properties
    ```

### 2. Variables usadas en el proyecto


Actualmente el proyecto utiliza las siguientes propiedades sensibles:

 - MY_SECRET_API_KEY → API key usada internamente en el build.gradle.kts.

Estas propiedades se leen desde:

 - secrets.properties en local, o

 - variables de entorno (por ejemplo, en CI/CD).

Si falta alguna, el build fallará con un mensaje indicando cuál no está definida.

### 3. Configuración en CI/CD

En el entorno de integración continua no se usa secrets.properties.
En su lugar, se configuran secrets / variables de entorno con el mismo nombre.

Ejemplo (GitHub Actions):

1. Ir a: Settings → Secrets and variables → Actions → New repository secret.

2. Crear un secret:

 - Name: MY_SECRET_API_KEY

 - Value: la misma clave que usarías en secrets.properties.

En los workflows, Gradle leerá esa variable de entorno automáticamente.

### 4. Configuración de los workflows

Hay que añadir las nuevas variables creadas a los workflows yml
