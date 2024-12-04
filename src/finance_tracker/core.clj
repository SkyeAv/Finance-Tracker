; Skye Goetz 12/02/2024

(ns finance-tracker.core
  (:gen-class)
  (:require [finance-tracker.util.database :as db]))

(defn -check_args [args expected_args]
  (= count args expected_args))

(defn -args_error [command usage_error]
  (println (str "| error | " command " | " usage_error " |")) 
  (System/exit 1))

(defn -universal_processing [command args expected_args usage_error]
  (if (-check_args args expected_args)
      (db/-initialize_db)
      (-args_error command usage_error)))

(def valid_income_categories ["salary" "bonus" "gift" "interest" "other"])

(def valid_expense_categories ["food" "rent" "college" "healthcare" "entertainment" "other"])

(defn -analysis [])

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
                        amount (read-string (nth args 3))
                        for_what (nth args 4)
                        category (nth args 5)]
                    (-income date amount for_what category)))
        "expense" ((let [expected_args 5
                         usage_error "usage: lein run expense <date> <amount> <for_what> <category>"]
                     (-universal_processing command args expected_args usage_error))
                   (let [date (second args)
                         amount (read-string (nth args 3))
                         for_what (nth args 4)
                         category (nth args 5)]
                     (-expense date amount for_what category)))
      "theorhetical" ((let [expected_args 6
                            usage_error "usage: lein run theorhetical <command> <date> <amount> <for_what> <category>"]
                        (-universal_processing command args expected_args usage_error))
                      (let [command (second args)
                            date (nth args 3)
                            amount (read-string (nth args 4))
                            for_what (nth args 5)
                            category (nth args 6)]
                        (-theorhetical command date amount for_what category)))
      (do
        (println (str "| error | unsuppoerted <command> | " command " |"))
        (System/exit 1))))))