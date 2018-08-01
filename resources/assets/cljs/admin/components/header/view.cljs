(ns admin.components.header.view
  (:require [re-frame.core :as re-frame]
            [admin.components.user-info.view :as user-info]
            [admin.components.navigation.view :as navigation]))

(defn render []
  (let [user             (re-frame/subscribe [:user-information])
        is-loading-user? (re-frame/subscribe [:is-loading? :user])
        active-page      (re-frame/subscribe [:active-page])
        page-title       (re-frame/subscribe [:page-title])]
    (fn []
      [:div.header>div.container
       [:div.header-inner.header-md-height
        [navigation/render @active-page]
        [user-info/render @user @is-loading-user?]]])))
