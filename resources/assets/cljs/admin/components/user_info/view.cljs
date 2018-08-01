(ns admin.components.user-info.view
  (:require [re-frame.core :as re-frame]
            [cljss.core :refer-macros [defstyles]]))

(defstyles user-text
  []
  {:line-height "9px"})

(defn render [{:keys [name bio]} is-loading?]
  [:div.align-items-center.pull-right
   [:div.pull-left.p-r-10.fs-14.font-heading.hidden-md-down.m-t-5
    {:class (user-text)}
    [:div.text-right (if is-loading? "Fetching user..." name)]
    [:span.small bio]]

   [:div.dropdown.pull-right.sm-m-r-5
    [:button.profile-dropdown-toggle.btn-link {:type          :button
                                               :data-toggle   :dropdown
                                               :aria-expanded :false}
     [:img {:src   "/img/logo/logo-nopadding-white.png"
            :width 24
            :alt   name}]]

    [:div.dropdown-menu.dropdown-menu-right.profile-dropdown
     [:a.dropdown-item [:i.pg-settings_small] "Settings"]
     [:a.dropdown-item [:i.pg-outdent] "Feedback"]
     [:a.dropdown-item [:i.pg-signals] "Help"]
     [:a.dropdown-item.bg-master-lighter.clearfix
      {:href     "#"
       :on-click (fn [e]
                   (.preventDefault e)
                   (re-frame/dispatch [:logout-user]))}
      [:span.pull-left "Logout"]
      [:span.pull-right [:i.pg-power ""]]]]]])
