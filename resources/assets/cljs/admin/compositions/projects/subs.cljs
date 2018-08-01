(ns admin.compositions.projects.subs
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(defn projects [db]
  (:projects db))

(defn projects-count [projects]
  (count projects))

(defn gallery-collections [db]
  (:gallery-collections db))

(defn top-projects-from-active-gallery
  [active-gallery]
  (->> active-gallery
       :projects
       :items
       (filter (comp pos? :likes))
       (sort-by (comp < :likes))
       (take 10)))

(defn active-gallery-name [db]
  (:active-gallery-name db))

(defn active-gallery [[collections active]]
  (first (filter #(= active (:name %)) collections)))

(rf/reg-sub :projects projects)
(rf/reg-sub :active-gallery-name active-gallery-name)
(rf/reg-sub :gallery-collections gallery-collections)

(rf/reg-sub
 :projects-count
 #(rf/subscribe [:projects])
 projects-count)

(rf/reg-sub
 :active-gallery
 (fn [db]
   [(rf/subscribe [:gallery-collections]) (rf/subscribe [:active-gallery-name])])
 active-gallery)

(rf/reg-sub
 :top-projects-from-active-gallery
 #(rf/subscribe [:active-gallery])
 top-projects-from-active-gallery)
