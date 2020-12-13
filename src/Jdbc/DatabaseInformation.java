package Jdbc;

@Deprecated(forRemoval = true)
public class DatabaseInformation {

    private final String databaseProductName;
    private final String databaseProductVersion;
    private final String databaseDriverName;
    private final String databaseDriverVersion;

    private DatabaseInformation(InformationBuilder builder) {
        this.databaseDriverName = builder.databaseDriverName;
        this.databaseDriverVersion = builder.databaseDriverVersion;
        this.databaseProductName = builder.databaseProductName;
        this.databaseProductVersion = builder.databaseProductVersion;
    }

    public static class InformationBuilder {
        private String databaseProductName;
        private String databaseProductVersion;
        private String databaseDriverName;
        private String databaseDriverVersion;

        public InformationBuilder addDatabaseProductName(String databaseProductName) {
            this.databaseProductName = databaseProductName;
            return this;
        }

        public InformationBuilder addDatabaseProductVersion(String databaseProductVersion) {
            this.databaseProductVersion = databaseProductVersion;
            return this;
        }

        public InformationBuilder addDatabaseDriverName(String databaseDriverName) {
            this.databaseDriverName = databaseDriverName;
            return this;
        }

        public InformationBuilder addDatabaseDriverVersion(String databaseDriverVersion) {
            this.databaseDriverVersion = databaseDriverVersion;
            return this;
        }

        public DatabaseInformation build() {
            return new DatabaseInformation(this);
        }
    }

    public String getDatabaseProductName() {
        return databaseProductName;
    }

    public String getDatabaseProductVersion() {
        return databaseProductVersion;
    }

    public String getDatabaseDriverName() {
        return databaseDriverName;
    }

    public String getDatabaseDriverVersion() {
        return databaseDriverVersion;
    }
}
