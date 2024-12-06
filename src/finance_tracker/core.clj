; Skye Goetz 12/02/2024

(ns finance-tracker.core
  (:gen-class)
  (:require [finance-tracker.util.database :as db]))

(defn -check_args [args expected_args]
  (= (count args) expected_args))

(defn -args_error [command usage_error]
  (println (str "| error | " command " | " usage_error " |")) 
  (System/exit 1))

(defn -universal_processing [command args expected_args usage_error]
  (if (-check_args args expected_args)
      (db/-initialize_db)
      (-args_error command usage_error)))

(def valid_income_categories ["salary" "bonus" "gift" "interest" "other"])

(def valid_expense_categories ["food" "rent" "college" "healthcare" "entertainment" "other"])

(defn -format_percentage [percentage]
  (format "%.2f" percentage))

(defn -get_percentage [amount total]
  (if (zero? total)
    "0.00%"
    (-format_percentage (* 100 (/ amount total)))))

(defn -analysis []
  (let [sum_finances (db/-sum_finances)
        sum_incomes (db/-sum_incomes)
        sum_expenses (db/-sum_expenses)]
    (println (str "| total | " sum_finances " | " sum_incomes " | " sum_expenses " |"))
    (doseq [category valid_income_categories]
      (let [amount (db/-sum_by_category category)
            percentage (-get_percentage amount sum_incomes)]
        (println (str "| " category " | " amount " | " percentage " |"))))
    (doseq [category valid_expense_categories]
      (let [amount (db/-sum_by_category category)
            percentage (-get_percentage amount sum_expenses)]
        (println (str "| " category " | " amount " | " percentage " |")))))
  (let [sum_last_month (db/-sum_last_month)
        sum_income_last_month (db/-sum_income_last_month)
        sum_expense_last_month (db/-sum_expense_last_month)]
    (println (str "| last month | " sum_last_month " | " sum_income_last_month " | " sum_expense_last_month " |"))
    (doseq [category valid_income_categories]
      (let [amount (db/-sum_last_month_by_category category)
            percentage (-get_percentage amount sum_income_last_month)]
        (println (str "| " category " | " amount " | " percentage " |"))))
    (doseq [category valid_expense_categories]
      (let [amount (db/-sum_last_month_by_category category)
            percentage (-get_percentage amount sum_expense_last_month)]
        (println (str "| " category " | " amount " | " percentage " |"))))))

(defn -validate_income_category [category]
    (contains? valid_income_categories category))

(defn -income [date amount for_what category]
  (if (-validate_income_category category)
    (do
      (db/-insert_to_table "income" date amount for_what category)
      (-analysis))
    (do 
      (println (str "| error | unsupported category | " category " |"))
      (System/exit 1))))

(defn -validate_expense_category [category]
    (contains? valid_expense_categories category))
  
(defn -make_expense_negative [amount]
  (* -1 amount))

(defn -expense [date amount for_what category]
  (if (-validate_expense_category category) 
      (let [amount (-make_expense_negative amount)]
        (db/-insert_to_table "expense" date amount for_what category)
        (-analysis))
    (do 
      (println (str "| error | unsupported category | " category " |"))
      (System/exit 1))))

(defn -theorhetical [command date amount for_what category]
  (let [valid_theorhetical_commands ["income" "expense"]]
    (if (contains? valid_theorhetical_commands command)
        (if (not= command "income")
          (let [amount (-make_expense_negative amount)]
            (db/-insert_to_table "income" date amount for_what category)
            (-analysis)
            (db/-delete_from_table "expense" date amount for_what category))
          (do
            (db/-insert_to_table "expense" date amount for_what category)
            (-analysis)
            (db/-delete_from_table "income" date amount for_what category)))
      (do 
        (println (str "| error | unsupported command | " command " |"))
        (System/exit 1)))))

(defn -main [& args]
  (if (empty? args)
    (do
      (println (str "| error | usage | lein run <command> [parameters] |"))
      (System/exit 1))
    (let [command (first args)]
      (case command
        "analysis" ((let [expected_args 1
                          usage_error "usage: lein run analysis"]
                      (-universal_processing command args expected_args usage_error))
                    (-analysis))
        "income" ((let [expected_args 5
                        usage_error "usage lein run income <date> <amount> <for_what> <category>"]
                    (-universal_processing command args expected_args usage_error))
                  (let [date (second args)
                        amount (read-string (nth args 2))
                        for_what (nth args 3)
                        category (nth args 4)]
                    (-income date amount for_what category)))
        "expense" ((let [expected_args 5
                         usage_error "usage: lein run expense <date> <amount> <for_what> <category>"]
                     (-universal_processing command args expected_args usage_error))
                   (let [date (second args)
                         amount (read-string (nth args 2))
                         for_what (nth args 3)
                         category (nth args 4)]
                     (-expense date amount for_what category)))
      "theoretical" ((let [expected_args 6
                            usage_error "usage: lein run theoretical <command> <date> <amount> <for_what> <category>"]
                        (-universal_processing command args expected_args usage_error))
                      (let [command (second args)
                            date (nth args 2)
                            amount (read-string (nth args 3))
                            for_what (nth args 4)
                            category (nth args 5)]
                        (-theorhetical command date amount for_what category)))
      (do
        (println (str "| error | unsuppoerted <command> | " command " |"))
        (System/exit 1))))))
