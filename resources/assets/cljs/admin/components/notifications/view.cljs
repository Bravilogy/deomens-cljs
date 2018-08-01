(ns admin.components.notifications.view)

(defn render []
  [:div
   [:ul.hidden-md-down.notification-list.no-margin.hidden-sm-down.b-grey.b-l.b-r.no-style.p-l-0.p-r-20
    [:li.li.p-r-10.inline
     [:div.dropdown
      [:a.header-icon.pg.pg-world [:span.bubble]]]]

    [:li.li.p-r-10.inline
     [:div.dropdown
      [:a.header-icon.pg.pg-link]]]

    [:li.li.p-r-10.inline
     [:div.dropdown
      [:a.header-icon.pg.pg-thumbs]]]]])
