(ns two-a-day.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [file-response]]
            [ring.middleware.json :refer [wrap-json-response]]))

(defroutes app-routes
  (GET "/" []
    (file-response "resources/public/index.html"))
  (GET "/api/weeks/previous" []
    {:body [{:y 2014 :w 43}
            {:y 2014 :w 44}
            {:y 2014 :w 45}
            {:y 2014 :w 46}
            {:y 2014 :w 47}
            {:y 2014 :w 48}
            {:y 2014 :w 49}
            {:y 2014 :w 50}
            {:y 2014 :w 51}]})
  (GET "/api/weeks/current" []
    {:body {:w 52
            :days [{:y 2014
                    :E "mon"
                    :content "one two"
                    :m 12
                    :d 20}
                   {:y 2014
                    :E "tue"
                    :content "three four"
                    :m 12
                    :fav true
                    :d 21}
                   {:y 2014
                    :E "wed"
                    :today true
                    :m 12
                    :d 21}]}})
  (GET "/api/weeks/:week" [week]
    {:body {:w (Integer/parseInt week)
            :y 2014
            :days [{:E "mon"
                    :content "one two"
                    :m 12
                    :d 20}
                   {:E "tue"
                    :content "three four"
                    :m 12
                    :fav true
                    :d 21}]}})
  (POST "/api/weeks/:week" [week]
    week)
  (POST "/api/days/:year/:month/:day" [year month day fav]
    {:body [year month day]})
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (wrap-json-response app-routes))
