(ns two-a-day.core.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [ring.util.response :refer [file-response]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]))

(defroutes app-routes
  (GET "/" []
    (file-response "resources/public/index.html"))
  (GET "/api/last-week" []
    {:body [{:date "2014-12-26"
             :E "mon"
             :content "one two"
             :today true}
            {:date "2014-12-25"
             :E "sun"
             :content "three four"}
            {:date "2014-12-24"
             :E "sat"
             :content "five six"
             :fav true}
            {:date "2014-12-23"
             :E "fri"
             :content "seven eight"}
            {:date "2014-12-22"
             :E "thu"
             :content "nine ten"}
            {:date "2014-12-21"
             :E "wed"
             :content "evelen twelve"
             :fav true}
            {:date "2014-12-20"
             :content "thirteen fourteen"
             :E "tue"}]})
  (GET "/api/faves" [before]
    {:body [{:date "2014-12-26"
             :E "fri"
             :content "zero zero"
             :fav true}
            {:date "2014-12-25"
             :E "thu"
             :content "zero zero"
             :fav true}
            {:date "2014-11-30"
             :E "mon"
             :content "zero zero"
             :fav true}
            {:date "2014-11-24"
             :E "tue"
             :content "zero zero"
             :fav true}]})
  (POST "/api/day/:date" [date content fav]
    (str date " -> " content (when fav " <3") "\n"))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> app-routes
    (wrap-params)
    (wrap-json-response)))

