import React, { useContext, useEffect } from 'react';
import Settings from './Settings';
import {
	Switch,
	Route,
	Link,
	Redirect,
	useHistory,
	useRouteMatch
} from 'react-router-dom';

import { ContentPage, NavBar } from '../styled/Components';
import { AuthContext } from '../App';
import UserList from './UserList';

/** The user page after log in */
const Admin = () => {
	const history = useHistory();
	const authContext = useContext(AuthContext);
	useEffect(() => {
		if (!authContext.authenticated) {
			authContext.signOut();
			history.push("/");
		}
	});
	const { url, path } = useRouteMatch();
	return (
		<div className="flex flex-row w-full h-full bg-indigo-900 text-gray-200">
			<NavBar>
				<Link to={`${url}/home`}
					className="mt-auto flex flex-row justify-center group-hover:justify-start group-hover:mr-3
						hover:bg-purple-800 text-gray-300 p-4 rounded-r-xl">
					<i className="material-icons">home</i>
					<p className="hidden group-hover:block mx-2 whitespace-nowrap">Home</p>
				</Link>
				<Link to={`${url}/settings`} className="flex flex-row justify-center group-hover:justify-start 
					group-hover:mr-3 hover:bg-purple-800 text-gray-300 p-4 rounded-r-xl">
					<i className="material-icons">settings</i>
					<p className="hidden group-hover:block mx-2">Settings</p>
				</Link>
				<Link to={`/`} className="mt-auto mb-2 flex flex-row justify-center 
					group-hover:mr-3 group-hover:justify-start hover:bg-purple-800 text-gray-300 p-4 rounded-r-xl">
					<i className="material-icons">logout</i>
					<p className="hidden group-hover:block mx-2 whitespace-nowrap">Log out</p>
				</Link>
			</NavBar>

			<ContentPage>
				<Switch>
					<Route path={`${path}/users`}>
						<UserList />
					</Route>
					<Route path={`${path}/settings`}>
						<Settings />
					</Route>

					<Route path={`${path}/`}>
						<Redirect to={`${url}/users`} />
					</Route>
				</Switch>
			</ContentPage>
		</div>
	);
}
export default Admin;
