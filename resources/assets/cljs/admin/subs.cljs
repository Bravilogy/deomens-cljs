(ns admin.subs
  (:require [re-frame.core :as rf]
            [admin.compositions.project.subs]
            [admin.compositions.projects.subs]
            [admin.compositions.galleries.subs]
            [admin.compositions.dashboard.subs]))

(defn active-page
  [db]
  (:active-page db))

(defn scroll-top
  [db]
  (get-in db [:ui :scroll-top]))

(defn user-information [db]
  (:user db {}))

(defn is-loading? [db [_ & path]]
  (let [full-path (into [:loading] path)]
    (get-in db full-path)))

(defn page-title [db]
  (:page-title db))

(defn breadcrumb [db]
  (:breadcrumb db))

(defn form-state
  [db [_ & which?]]
  (get-in db (concat [:forms] which?)))

(rf/reg-sub :form-state form-state)
(rf/reg-sub :scroll-top scroll-top)
(rf/reg-sub :breadcrumb breadcrumb)
(rf/reg-sub :page-title page-title)
(rf/reg-sub :is-loading? is-loading?)
(rf/reg-sub :active-page active-page)
(rf/reg-sub :user-information user-information)
