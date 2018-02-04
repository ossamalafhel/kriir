import React from 'react'
import ReactDOM from 'react-dom'
import App from './component/app'
import store from './reducers/store'
import startup from './actions/startup'
import './index.css'

const d = document
const rootElement = d.querySelector('#root')

const reactRender = () => {
  ReactDOM.render(
    <App />,
    rootElement
  )
}

reactRender()

store.subscribe((evt) =>
  reactRender()
)

startup()
