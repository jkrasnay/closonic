(ns ^:figwheel-always closonic.core
    (:require[om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state
  (atom {:playlist [{:track "Pillar of Salt" :artist "The Thermals" :album "The Body, the Blood, the Machine"}
                    {:track "Driver 8" :artist "REM" :album "Eponymous" }
                    {:track "Alone+Easy Target" :artist "Foo Fighters" :album "Foo Fighters" }]
         :current 0}))

(defn playlist-item [item owner]
  "Renders a single item in the playlist"
  (reify
    om/IRender
    (render [_]
      (dom/li #js {:className "Playlist-item"}
              (str (:track item) " - " (:artist item))))))

(defn playlist [data owner]
  "Renders the playlist below the 'currently playing' bit."
  (reify
    om/IRender
    (render [_]
      (println "render playlist" data)
      (apply dom/ul #js {:className "Playlist"}
             (om/build-all playlist-item (:playlist data))))))

(defn now-playing [data owner]
  "Renders the 'now playing' block. Data is the entire app data"
  (reify
    om/IRender
    (render [_]
      (let [current ((:playlist data) (:current data))]
      (dom/div #js {:className "NowPlaying"}
               (dom/div #js {:className "NowPlaying-track"} (:track current))
               (dom/div #js {:className "NowPlaying-artist"} (:artist current))
               (dom/div #js {:className "NowPlaying-album"} (:album current)))))))

(defn playlist-view [data owner]
  "Renders the playlist view. Data is the entire app state"
  (reify
    om/IRender
    (render [_]
      (println "render playlist view" (:playlist data))
      (dom/div #js {:className "PlaylistView"}
               (om/build now-playing data)
               (om/build playlist data)))))

(om/root
  playlist-view
  app-state
  {:target (. js/document (getElementById "app"))})


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

