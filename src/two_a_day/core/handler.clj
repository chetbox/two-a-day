(ns two-a-day.core.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [ring.util.response :refer [file-response]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :as mq]
            monger.joda-time
            [clojure.string :as str]
            [clj-time.core :refer [today ago weeks]]
            [clj-time.format :as time-format]
            [clj-time.predicates :refer [same-date?]]))

(def config (read-string (slurp "config.edn")))

; Connect to database
(def db-conn (mg/connect (:db-host config)))
(def db (mg/get-db db-conn "two-a-day"))

(def date-format (time-format/formatter "yyyy-MM-dd"))
(def day-format (time-format/formatter "EEE"))

(def by-latest-first (array-map :date -1))

(defn str->date
  [s]
  (time-format/parse date-format s))

(defn json-friendly-day-map
  [doc]
  (merge {:date (time-format/unparse date-format (:date doc))
          :day (time-format/unparse day-format (:date doc))
          :content (:content doc)}
         (when (:fav doc)
           {:fav true})
         (when (same-date? (today) (:date doc))
           {:today true})))

(defroutes app-routes
  (GET "/" []
    (file-response "resources/public/index.html"))
  (GET "/api/last-week" []
    {:body (map json-friendly-day-map
                (mq/with-collection db "days"
                  (mq/find {:date {"$lte" (today)
                                   "$gt" (ago (weeks 1))}})
                  (mq/sort by-latest-first)))})
  (GET "/api/faves" [before]
    {:body (map json-friendly-day-map
                (mq/with-collection db "days"
                  (mq/find (if before
                             {:date {"$lt" (str->date before)}}
                             {}))
                  (mq/sort by-latest-first)
                  (mq/limit 10)))})
  (POST "/api/today" [content]
    (mc/update db "days" {:date (today)} {"$set" {:content content}} {:upsert true})
    "ok")
  (POST "/api/day/:date-str" [date-str fav]
    (mc/update db "days" {:date (str->date date-str)} {"$set" {:fav (= fav "true")}})
    "ok")
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
    (wrap-params)
    (wrap-json-response)))

