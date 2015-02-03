(ns two-a-day.core.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [ring.util.response :refer [file-response]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [taoensso.faraday :as far]
            [clojure.string :as str]
            [clj-time.core :as time]
            [clj-time.format :as time-format]
            [clj-time.predicates :refer [same-date?]]))


(def config (read-string (slurp "config.edn")))

(def my-user-id "me") ; TODO: implement login

(defn today
  []
  "2015-01-31") ; TODO: implement me

(defn one-week-ago
  []
  "2015-01-24") ; TODO: implement me

; Database setup
(far/ensure-table (:dynamodb config)
                  (:posts-table config)
                  [:user-id :s]
                  {:range-keydef [:date :s]
                   :throughput {:read 1
                                :write 1}
                   :block? true})

(defroutes app-routes
  (GET "/" []
    (file-response "resources/public/index.html"))
  (GET "/api/last-week" []
    {:body (far/query (:dynamodb config)
                      (:posts-table config)
                      {:user-id [:eq my-user-id]
                       :date [:between [(one-week-ago) (today)]]})}) ; TODO: sort by date
  (GET "/api/faves" [before]
    {:body (far/query (:dynamodb config)
                      (:posts-table config)
                      (merge
                        {:user-id [:eq my-user-id]
                         :fav [:eq 1]}
                        (when before
                          {:date [:lt before]}))
                      :limit 10)}) ; TODO: sort by date
  (POST "/api/day/:date" [date content fav]
    (assert (or content fav))
    (far/update-item
      (:dynamodb config)
      (:posts-table config)
      {:user-id my-user-id
       :date date}
      (merge
        (when content
          {:content [:put content]})
        (when fav
          {:fav [:put (if (= fav "true") 1 0)]})))
    "ok\n")
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
    (wrap-params)
    (wrap-json-response)))

