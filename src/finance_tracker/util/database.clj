; Skye Goetz 12/02/2024

(ns finance-tracker.util.database
  (:gen-class)
  (:require [clojure.java.jdbc :as jdbc]))

(defn conn []
  (let [db_path "finance_tracker/resources/finance_tracker.db"]
  {:dbtype "sqlite" :dbname db_path}))

(defn -initialize_db []
  (let [create_tables 
        ["CREATE TABLE IF NOT EXISTS income (date TEXT PRIMARY KEY, income FLOAT, for_what TEXT, category TEXT)"
         "CREATE TABLE IF NOT EXISTS expense (date TEXT PRIMARY KEY, expense FLOAT, for_what TEXT, category TEXT)"
         "CREATE INDEX IF NOT EXISTS income_index ON income (date)"
         "CREATE INDEX IF NOT EXISTS income_index_twoON income (category)"
         "CREATE INDEX IF NOT EXISTS expense_index ON expense (date)"
         "CREATE INDEX IF NOT EXISTS expense_index_two ON expense (category)"]]
    (doseq [query create_tables]
      (jdbc/execute! (conn) query))))

(defn -insert_to_table [table date amount for_what category]
  (let [query (str "INSERT INTO " table " (date, amount, for_what, category) VALUES (\"" date "\", " amount ", \"" for_what "\", \"" category "\")")]
    (jdbc/execute! (conn) query)))

(defn -delete_from_table [table date amount for_what category]
  (let [query (str "DELETE FROM " table " WHERE " date " = " amount " AND for_what = \"" for_what "\" AND category = \"" category "\"")]
    (jdbc/execute! (conn) query)))

(defn -query_table [query]
  (jdbc/query (conn) query))

(defn -sum_finances []
  (let [query "SELECT SUM(amount) FROM income UNION ALL SELECT SUM(amount) FROM expense"]
    (jdbc/query (conn) query)))

(defn -sum_by_category [category]
  (let [query (str "SELECT SUM(amount) FROM income WHERE category = \"" category "\" UNION ALL SELECT SUM(amount) FROM expense WHERE category = \"" category "\"")]
    (jdbc/query (conn) query)))