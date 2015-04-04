(ns two-a-day.core.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [ring.util.response :refer [file-response]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [taoensso.faraday :as far]
            [two-a-day.core.date-utils :refer [today one-week-ago]]))


(def config (read-string (slurp "config.edn")))

(def my-user-id "me") ; TODO: implement login

(print "Setting up database table... ")
(far/ensure-table (:dynamodb config)
                  (:posts-table config)
                  [:user-id :s]
                  {:range-keydef [:date :s]
                   :throughput {:read 1
                                :write 1}
                   :block? true})
(println "Done.")

(defroutes app-routes
  (GET "/" []
    (file-response "resources/public/index.html"))
  (GET "/api/last-week" []
    {:body (far/query (:dynamodb config)
                      (:posts-table config)
                      {:user-id [:eq my-user-id]
                       :date [:between [(one-week-ago) (today)]]}
                      {:order :desc})})
  (GET "/api/faves" [before]
    {:body (far/query (:dynamodb config)
                      (:posts-table config)
                      (merge
                        {:user-id [:eq my-user-id]}
                        (when before
                          {:date [:lt before]}))
                      {:query-filter {:fav [:eq 1]}
                       :order :desc
                       :limit 20})})
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
