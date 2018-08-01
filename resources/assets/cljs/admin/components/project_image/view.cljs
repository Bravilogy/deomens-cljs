(ns admin.components.project-image.view
  (:require ["react-dropzone" :as drop-zone]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [cljss.core :refer-macros [defstyles]]))

(defn render
  "this component handles thumbnail / display and featured images"
  [{:keys [current-image on-update on-delete width] :or {width "100%"}}]
  (if current-image
    [:div.text-center.m-t-10.col-6.offset-3
     [:p
      [:img {:src   current-image
             :class :img-thumbnail
             :width width}]]

     [:p
      [:button.btn.btn-danger.btn-sm
       {:on-click on-delete}
       "Remove current"]]]

    [:div.dropzone
     [:> drop-zone {:on-drop   on-update
                    :multiple  false
                    :className "dz-default dz-message"}]]))
