; Skye Goetz 12/02/2024

(ns finance-tracker.util.database
  (:gen-class)
  (:require [clojure.java.jdbc :as jdbc]
            [clj-time.core :as t]
            [clj-time.format :as tf]))

(defn conn []
  (let [db_path "finance_tracker/resources/finance_tracker.db"]
  {:dbtype "sqlite" :dbname db_path}))

(defn -exexecute! [query]
   (jdbc/execute! (conn) query))

(defn -initialize_db []
  (let [create_tables 
        ["CREATE TABLE IF NOT EXISTS income (date TEXT PRIMARY KEY, income FLOAT, for_what TEXT, category TEXT)"
         "CREATE TABLE IF NOT EXISTS expense (date TEXT PRIMARY KEY, expense FLOAT, for_what TEXT, category TEXT)"
         "CREATE INDEX IF NOT EXISTS income_index ON income (date)"
         "CREATE INDEX IF NOT EXISTS income_index_twoON income (category)"
         "CREATE INDEX IF NOT EXISTS expense_index ON expense (date)"
         "CREATE INDEX IF NOT EXISTS expense_index_two ON expense (category)"]]
    (doseq [query create_tables]
      (-exexecute!( query)))))

(defn -insert_to_table [table date amount for_what category]
  (let [query (str "INSERT INTO " table " (date, amount, for_what, category) VALUES (\"" date "\", " amount ", \"" for_what "\", \"" category "\")")]
    (-exexecute! query)))

(defn -delete_from_table [table date amount for_what category]
  (let [query (str "DELETE FROM " table " WHERE " date " = " amount " AND for_what = \"" for_what "\" AND category = \"" category "\"")]
    (-exexecute! query)))

(defn -query_table [query]
  (first (jdbc/query (conn) query)))

(defn -sum_incomes []
  (let [query "SELECT SUM(amount) FROM income"]
    (-query_table query)))

(defn -sum_expenses []
  (let [query "SELECT SUM(amount) FROM expense"]
    (-query_table query)))

(defn -sum_finances []
  (let [query "SELECT SUM(amount) FROM income UNION ALL SELECT SUM(amount) FROM expense"]
    (-query_table query)))

(defn -sum_by_category [category]
  (let [query (str "SELECT SUM(amount) FROM income WHERE category = \"" category "\" UNION ALL SELECT SUM(amount) FROM expense WHERE category = \"" category "\"")]
    (-query_table query)))

(defn -get-first-and-last-dates-of-month []
  (let [now (t/now)
        first-date (t/first-day-of-the-month now)
        last-date (t/last-day-of-the-month now)]
    [(tf/unparse (tf/formatter "yyyy-MM-dd") first-date)
     (tf/unparse (tf/formatter "yyyy-MM-dd") last-date)]))

(defn -sum_last_month []
  (let [first_date (first (-get-first-and-last-dates-of-month))
        last_date (second (-get-first-and-last-dates-of-month))
        query (str "SELECT SUM(amount) FROM income UNION ALL SELECT SUM(amount) FROM expense WHERE date > \"" first_date "\" AND date < \"" last_date "\"")]
      (-query_table query)))

(defn -sum_income_last_month []
  (let [first_date (first (-get-first-and-last-dates-of-month))
        last_date (second (-get-first-and-last-dates-of-month))
        query (str "SELECT SUM(amount) FROM income WHERE date > \"" first_date "\" AND date < \"" last_date "\"")]
    (-query_table query)))

(defn -sum_expense_last_month []
  (let [first_date (first (-get-first-and-last-dates-of-month))
        last_date (second (-get-first-and-last-dates-of-month))
        query (str "SELECT SUM(amount) FROM expense WHERE date > \"" first_date "\" AND date < \"" last_date "\"")]
    (-query_table query)))

(defn -sum_last_month_by_category [category]
  (let [first_date (first (-get-first-and-last-dates-of-month))
        last_date (second (-get-first-and-last-dates-of-month))
        query (str "SELECT SUM(amount) FROM income UNION ALL SELECT SUM(amount) FROM expense WHERE date > \"" first_date "\" AND date < \"" last_date "\" AND category = \"" category "\"")]
      (-query_table query)))