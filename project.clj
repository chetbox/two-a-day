(defproject two-a-day "0.1.0-SNAPSHOT"
  :description "Just two words a day"
  :url "http://github.com/chetbox/two-a-day"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [ring/ring-json "0.3.1"]
                 [com.taoensso/faraday "1.5.0"]
                 [clj-time "0.9.0"]]
  :plugins [[lein-ring "0.8.13"]]
  :ring {:handler two-a-day.core.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}})
