(ns admin.db
  (:require [re-frame.core :as rf]))

(defn- get-meta-content
  "get a meta tag content"
  [selector]
  (.-content (.querySelector js/document selector)))

;; a few good co-effects
(rf/reg-cofx
 :client-token-meta
 (fn [cofx _]
   (assoc cofx :csrf_token (get-meta-content "[name=csrf]"))))

(defonce default-db
  {:active-page :dashboard
   :page-title  ""
   :breadcrumb  []})
