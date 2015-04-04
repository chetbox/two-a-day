(ns two-a-day.core.date-utils
  (:require [clj-time.core :as time]
            [clj-time.format :as time-format]))

(def date-formatter (time-format/formatters :date))

(defn date->str
  [date]
  (time-format/unparse date-formatter date))

(defn today
  []
  (date->str (time/today-at-midnight)))

(defn one-week-ago
  []
  (date->str (time/minus (time/today-at-midnight)
                         (time/weeks 1))))
