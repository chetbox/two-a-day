(ns two-a-day.core.handler
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :as route]
            [clojure.pprint :refer [pprint]]
            [ring.util.response :refer [file-response redirect]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [taoensso.faraday :as far]
            [two-a-day.core.date-utils :refer [today one-week-ago]]
            [two-a-day.core.auth :as auth]
            [two-a-day.db.posts :refer [posts-api]]
            [two-a-day.db.users :refer [users-api new-user-id]]
            [oauth.client :as oauth]))


(def config (read-string (slurp "config.edn")))

(def posts (posts-api (:dynamodb config)))
(def users (users-api (:dynamodb config)))

(defn get-local-user
  [access-token-response]
  (let [local-user-id (or (:local-user-id (first (users :query {:user_id [:eq (:user_id access-token-response)]})))
                          (new-user-id))]
    (users :update-item
           (select-keys access-token-response [:user_id])
           (into {:local-user-id [:put local-user-id]}
             (map (fn [[k v]] [k [:put v]])
                  (dissoc access-token-response :user_id))))
      local-user-id))

(print "Creating Twitter consumer... ")
(def twitter-auth-consumer (auth/make-twitter-consumer (:twitter-app config)))
(println "Done.")


(defroutes app-routes

  (GET "/" [user-id]
    (if user-id
      (file-response "resources/index.html")
      (redirect "/login/twitter")))

  (GET "/login/twitter" []
    (let [request-token (oauth/request-token twitter-auth-consumer "http://localhost:3000/login/twitter/oauth_callback")]
      (auth/put-request-token (:oauth_token request-token)
                              request-token)
      (redirect (oauth/user-approval-uri twitter-auth-consumer
                                         (:oauth_token request-token)))))

  (GET "/login/twitter/oauth_callback" [oauth_token oauth_verifier]
    (let [request-token (auth/pop-request-token oauth_token)
          access-token-response (oauth/access-token twitter-auth-consumer
                                                    request-token
                                                    oauth_verifier)
          local-user-id (get-local-user access-token-response)]
      (auth/set-user-cookie (redirect "/")
                            local-user-id)))

  (GET "/api/last-week" [user-id]
    {:body (posts :query
             {:user-id [:eq user-id]
              :date [:between [(one-week-ago) (today)]]}
             {:order :desc})})

  (GET "/api/faves" [before user-id]
    {:body (posts :query
             (merge
               {:user-id [:eq user-id]}
               (when before
                 {:date [:lt before]}))
             {:query-filter {:fav [:eq 1]}
              :order :desc
              :limit 20})})

  (POST "/api/day/:date" [date content fav user-id]
    (assert (or content fav))
    (posts :update-item
      {:user-id user-id
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
    auth/wrap-auth
    wrap-params
    wrap-json-response
    wrap-cookies))
