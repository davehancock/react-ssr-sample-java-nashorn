import React from 'react';
import ReactDOMServer from 'react-dom/server'
import {StaticRouter} from 'react-router'
import serialize from 'serialize-javascript';

import App from './components/app'


window.render = (template, model) => {

    const location = model.get('currentPath');
    const routerContext = {};

    const initialState = JSON.parse(model.get('serverSideState'));

    const markup = ReactDOMServer.renderToString(
        <StaticRouter location={location} context={routerContext}>
            <App store={initialState}/>
        </StaticRouter>
    );

    return template
        .replace('SERVER_RENDERED_HTML', markup)
        .replace('SERVER_RENDERED_STATE', serialize(initialState, {isJSON: true}));
};
