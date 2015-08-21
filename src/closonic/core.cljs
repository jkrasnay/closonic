(ns ^:figwheel-always closonic.core
    (:require[om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state
  (atom {:playlist [{:id 1 :track "Pillar of Salt" :artist "The Thermals" :album "The Body, the Blood, the Machine"}
                    {:id 2 :track "Driver 8" :artist "REM" :album "Eponymous" }
                    {:id 3 :track "Alone+Easy Target" :artist "Foo Fighters" :album "Foo Fighters" }]
         :current 2}))

;; control functions
;;

(defn play-song [index]
  "Plays the song with the given ID. The song must already be in the playlist.
   Not sure, but it seems we should pass the app-state atom in here."
  (swap! app-state assoc :current index))

(defn play-next []
  (play-song (min (inc (:current @app-state)) (dec (count (:playlist @app-state))))))

(defn play-prev []
  (play-song (max (dec (:current @app-state)) 0)))


;; UI components
;;

(defn playlist-item [item owner]
  "Renders a single item in the playlist. Item is a map with :data holding the app state and :index holding the index of this song"
  (reify
    om/IRender
    (render [_]
      (let [song ((:playlist (:data item)) (:index item))]
        (dom/li #js {:className (str "Playlist-item" (when (= (:index item) (:current (:data item))) " is-playing"))
                     :onClick (fn [_] (play-song (:index item)))}
                (str (:track song) " - " (:artist song)))))))

(defn playlist [data owner]
  "Renders the playlist below the 'currently playing' bit."
  (reify
    om/IRender
    (render [_]
      (apply dom/ul #js {:className "Playlist"}
             (om/build-all playlist-item (map #(hash-map :index % :data data) (range (count (:playlist data)))))))))

(defn now-playing [data owner]
  "Renders the 'now playing' block. Data is the entire app data"
  (reify
    om/IRender
    (render [_]
      (let [song ((:playlist data) (:current data))]
        (dom/div #js {:className "NowPlaying"}
                 (dom/div #js {:className "NowPlaying-track"} (:track song))
                 (dom/div #js {:className "NowPlaying-artist"} (:artist song))
                 (dom/div #js {:className "NowPlaying-album"} (:album song)))))))

(defn playlist-view [data owner]
  "Renders the playlist view. Data is the entire app state"
  (reify
    om/IRender
    (render [_]
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

