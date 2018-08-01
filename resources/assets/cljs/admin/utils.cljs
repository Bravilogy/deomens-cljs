(ns admin.utils
  (:require ["moment" :as moment]
            [clojure.string :refer [split trim lower-case join replace]]))

(def api-url "/api")

(defn create-converter
  "generic function that can be used in filters
  to update a map that matches a predicate"
  [[match-key match-value]
   replace-key replace-value]

  (fn [needle]
    (if (= match-value (match-key needle))
      (assoc needle replace-key replace-value)
      needle)))

(defn- compact-spaces
  [raw]
  (join " "
        (split
         (trim raw) #"\s+")))

(defn slugify
  "Slugifies a given string"
  [raw]
  (-> raw
      lower-case
      compact-spaces
      (replace " " "-")
      (replace #"[^a-z0-9-]" "")))

(defn to-timestamp
  "convert js date object to timestamp"
  [js-date]
  (js/Math.round (/ (.getTime js-date) 1000)))

(defn unix-from-now
  "convert unix timestamp to
  human readable string"
  [unix]
  (.fromNow (.unix moment unix)))

(defn endpoint [& params]
  (join "/" (concat [api-url] params)))

(defn with-auth
  "this function will add all the necessary
  headers to the request to auth an api call"
  [db]
  (when-let [token (get-in db [:client :csrf_token])]
    {:X-Requested-With :XMLHttpRequest
     :X-XSRF-TOKEN     token
     :X-CSRF-TOKEN     token}))

(defn upload-request-body
  "this function will convert maps
  into FormData objects"
  [params]
  (let [fd (new js/FormData)]
    (doseq [[k v] params]
      (.append fd (name k) v))
    fd))
