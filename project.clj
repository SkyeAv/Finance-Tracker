; Skye Goetz 12/02/2024

(defproject finance_tracker "1.0.0"
  :description "Non-serious Project To Track My Finances"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.xerial/sqlite-jdbc "3.42.0.0"]
                 [clj-time "0.5.0"]]
  :main ^:skip-aot finance-tracker.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
