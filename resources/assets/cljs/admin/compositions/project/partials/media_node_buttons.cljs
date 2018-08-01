(ns admin.compositions.project.partials.media-node-buttons)

(defn view
  [{on-button-click :on-button-click}]
  (let [buttons [{:type :video
                  :icon :fa-play-circle}
                 {:type :image
                  :icon :fa-camera}
                 {:type :text
                  :icon :fa-text-width}]]
    [:div.row
     (for [{:keys [icon type]} buttons]
       ^{:key type}
       [:div.col-4
        [:div.form-group
         [:button.btn.btn-default.btn-block.btn-sm.text-uppercase
          {:on-click #(on-button-click type)}
          [:span.pull-left
           [:i.fa {:class icon}]]
          "New " type " node"]]])]))
