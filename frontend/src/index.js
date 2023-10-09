import React from 'react'
import ReactDOM from 'react-dom/client'
import Root from './Root.jsx'
import './index.css'
import '@fontsource/roboto/300.css';
import '@fontsource/roboto/400.css';
import '@fontsource/roboto/500.css';
import '@fontsource/roboto/700.css';
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import JoinGame from "./JoinGame.jsx";
import CreateGame from "./CreateGame.jsx";
import Game from "./Game.jsx";
import {StompSessionProvider} from "react-stomp-hooks";
import CreatePlayer from "./CreatePlayer.jsx";

const router = createBrowserRouter([
    {
        path: '/',
        element: <Root />
    },
    {
        path: '/create-player',
        element: <CreatePlayer />
    },
    {
        path: '/create-game',
        element: <CreateGame />
    },
    {
        path: '/join-game',
        element: <JoinGame />
    },
    {
        path: '/game/:gameId',
        element: <Game />
    }
])

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
      <StompSessionProvider url={`${window.location.protocol === 'https:' ? 'wss:' : 'ws:'}//${window.location.hostname}:8080/ws`}>
          <RouterProvider router={router} />
      </StompSessionProvider>
  </React.StrictMode>,
)
