(ns two-a-day.core.auth
  (:require [oauth.client :as oauth]))

(defn make-twitter-consumer
  [{:keys [consumer-key consumer-secret]}]
  (oauth/make-consumer consumer-key
                       consumer-secret
                       "https://api.twitter.com/oauth/request_token"
                       "https://api.twitter.com/oauth/access_token"
                       "https://api.twitter.com/oauth/authorize"
                       :hmac-sha1))

(def request-tokens (atom {})) ; TODO : remove after timeout

(defn push-request-token
  [k v]
  (swap! request-tokens
         assoc k v)
  val)

(defn pop-request-token
  [k]
  (let [v (get @request-tokens k)]
    (swap! request-tokens
           dissoc k)
    v))

(defn wrap-auth
  [app]
  (fn [request]
    (app
      (assoc-in request
                [:params :user-id]
                (get-in request [:cookies "user-id" :value])))))

(defn set-user-cookie
  [response user-id]
  (assoc response
    :cookies {"user-id" {:value user-id
                         :path "/"}}))
