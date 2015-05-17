(defproject two-a-day "0.1.1"
  :description "Just two words a day"
  :url "http://github.com/chetbox/two-a-day"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [compojure "1.3.3"]
                 [ring/ring-json "0.3.1"]
                 [com.taoensso/faraday "1.6.0"]
                 [clj-time "0.9.0"]
                 [clj-oauth "1.5.2"]
                 [http-kit "2.1.16"]]
  :plugins [[lein-ring "0.9.3"]]
  :ring {:handler two-a-day.core.handler/app}
  :main two-a-day.core.handler
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}
   :uberjar {:aot :all}})
