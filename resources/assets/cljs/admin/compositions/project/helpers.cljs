(ns admin.compositions.project.helpers
  (:require [re-frame.core :as rf]
            [admin.utils :refer [slugify]]
            [cljss.core :refer-macros [defstyles]]
            ))

(defstyles slug-style
  []
  {:cursor      "pointer"
   :margin-left "3px"})

(defn title-blur-handler
  [e slug]
  (when (empty? slug)
    (rf/dispatch [:update-form-field
                  :update-project
                  :slug
                  (slugify (-> e .-target .-value))])))

(defn project-image-update-handler
  [project-id type]
  (fn [images]
    (rf/dispatch [:upload-project-image
                  {:project-id project-id
                   :image-type type
                   :images     images}])))

(defn project-image-delete-handler
  [project-id type]
  (fn [e]
    (.preventDefault e)
    (rf/dispatch [:delete-project-image
                  {:project-id project-id
                   :image-type type}])))

(defn suggested-slug
  "generate a slug preview by slugifying title"
  [title]
  (if (not-empty title)
    (let [slug (slugify title)]
      [:span "suggested slug: "
       [:code {:class    (slug-style)
               :on-click #(rf/dispatch [:update-form-field
                                        :update-project
                                        :slug
                                        slug])}
        slug]])

    "Add title to see an auto generated slug"))
