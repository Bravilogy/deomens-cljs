(ns admin.components.navigation.view
  (:require [admin.routes :refer [dashboard
                                  projects
                                  galleries]]))

(def routes [{:label       "Dashboard"
              :route-fn    dashboard
              :route-names [:dashboard]}

             {:label       "Projects"
              :route-fn    projects
              :route-names [:projects :project]}

             {:label       "Galleries"
              :route-fn    galleries
              :route-names [:galleries]}
             ])

(defn render [current-route]
  [:div {:data-hide-extra-li 4
         :class              "menu-bar header-sm-height"
         :data-pages         :horizontal-menu}

   [:ul
    (map (fn [{:keys [label route-names route-fn]}]
           (let [active? (some #{current-route} route-names)]
             [:li {:key label
                   :class (when active? "active")}
              [:a {:class (when active? "active")
                   :href  (route-fn)}
               label]]))
         routes)]])
