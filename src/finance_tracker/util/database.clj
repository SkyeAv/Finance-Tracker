; Skye Goetz 12/02/2024

(ns finance-tracker.util.database
  (:gen-class)
  (:require [clojure.java.jdbc :as jdbc]))

(defn conn []
  (let [db_path "finance_tracker/resources/finance_tracker.db"]
  {:dbtype "sqlite" :dbname db_path}))

(defn -initialize_db []
  (let [create_tables 
        ["CREATE TABLE IF NOT EXISTS income (date TEXT PRIMARY KEY, income FLOAT, for_what TEXT)"
         "CREATE TABLE IF NOT EXISTS expense (date TEXT PRIMARY KEY, expense FLOAT, for_what TEXT)"
         "CREATE INDEX IF NOT EXISTS income_index ON income (date)"
         "CREATE INDEX IF NOT EXISTS expense_index ON expense (date)"]]
    (doseq [query create_tables]
      (jdbc/execute! (conn) query))))