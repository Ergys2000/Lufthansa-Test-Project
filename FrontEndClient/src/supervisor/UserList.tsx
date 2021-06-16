import { useContext, useEffect, useState } from 'react';
import { ContentPage, ContentPageContext, Popup, Label, Input, Select, ActionButton, Form, DangerButton } from '../styled/Components';
import { AuthContext } from '../App';
import apiLink from '../API';
import { Switch, Route, Link, useHistory, useRouteMatch, useParams, useLocation } from 'react-router-dom';
import { User, Request } from '../types/Common';
import RequestList from './RequestList';


/** The users page */
const Users = (props: any) => {
	const { url, path } = useRouteMatch();
	return (
		<Switch>

			<Route path={`${path}/:requestsUserId/`}>
				<UserRequestList />
			</Route>

			<Route path={`${path}/`}>
				<UserList />
			</Route>

		</Switch>
	);
}

/** 
* Displays the user list page
* @function UserList
* */
const UserList = (props: any) => {
	const { userId } = useParams() as any;
	const authContext = useContext(AuthContext);
	const { url } = useRouteMatch();

	/* Set the location list in the header of the page */
	const contentPageContext = useContext(ContentPageContext);
	useEffect(() => {
		contentPageContext.setLocationList([{ title: "User list", url: url }]);
	}, []);

	/* Set up the state that holds the users, and also a value for updating the
	* list with a callback*/
	const [users, setUsers] = useState<User[]>([]);
	useEffect(() => {
		const bearer = "Bearer " + authContext.jwtToken;
		fetch(`${apiLink}/supervisor/${userId}/users`, {
			headers: {
				'Authorization': bearer
			}
		})
			.then(res => res.json())
			.then(res => {
				if (res.status === "OK") {
					const usrs = res.result as User[];
					setUsers(usrs.filter(usr => usr.id != authContext.userId));
				} else {
					alert(res.message);
				}
			}).catch(err => console.log(err));
	}, [userId]);

	return (
		<div className="flex flex-col">
			<div className="mx-10 border-l-2 border-r-2 border-b-2 border-gray-800 bg-gray-100 text-gray-600">
				<div className="bg-gray-800 flex flex-row justify-center w-full text-gray-300 p-2">
					<p className="font-bold flex-1 text-center">Type</p>
					<p className="font-bold flex-1 text-center">Email</p>
					<p className="font-bold flex-1 text-center">Firstname</p>
					<p className="font-bold flex-1 text-center">Lastname</p>
					<p className="font-bold flex-1 text-center">See requests</p>
				</div>
				{users.map(user => <UserItem key={user.id} user={user} />)}
			</div>
		</div>
	);
}

/**
* Displays a user list item
* @function UserItem
* @param {Object} props - The props of the component
* @param {User} props.user - The user this item represents
* @param {User[]} props.supervisors - The list of available supervisors
* @returns A single user item for the user list
* */
const UserItem = (props: { user: User }) => {
	const { user } = props;
	const history = useHistory();
	const { url } = useRouteMatch();
	const onClick = () => {
		history.push(`${url}/${user.id}`, user);
	}
	return (
		<div className="flex flex-row justify-center w-full p-5 border-b border-gray-400">
			<p className="flex-1 text-center">{user.type}</p>
			<p className="flex-1 text-center">{user.email}</p>
			<p className="flex-1 text-center">{user.firstname}</p>
			<p className="flex-1 text-center">{user.lastname}</p>
			<div className="flex-1 flex justify-center items-center">
				<button onClick={onClick} className="focus:outline-none flex justify-center 
					items-center bg-transparent text-black hover:bg-gray-300 rounded-full p-1">
					<i className="material-icons hover:cursor-pointer">arrow_right</i>
				</button>
			</div>
		</div>
	);
}

/** Displays the requests of a user */
const UserRequestList = (props: any) => {
	const authContext = useContext(AuthContext);
	const { url } = useRouteMatch();
	const location = useLocation();
	const user: User = location.state as User;

	/* Set the location list in the header of the page */
	const contentPageContext = useContext(ContentPageContext);
	useEffect(() => {
		const locationList = contentPageContext.getLocationList();
		const name = `${user.firstname} ${user.lastname}`;
		contentPageContext.setLocationList([
			...locationList,
			{ title: name, url: url, state: user }
		]);
	}, []);

	/* Set up the state that holds the requests, and also a value for updating the
	* list with a callback*/
	const [requests, setRequests] = useState<Request[]>([]);
	useEffect(() => {
		const bearer = "Bearer " + authContext.jwtToken;
		fetch(`${apiLink}/supervisor/${authContext.userId}/users/${user.id}/requests`, {
			headers: {
				'Authorization': bearer
			}
		})
			.then(res => res.json())
			.then(res => {
				if (res.status === "OK") {
					setRequests(res.result);
				} else {
					alert(res.message);
				}
			}).catch(err => console.log(err));
	}, [authContext.userId, user.id]);


	return (
		<div className="flex flex-col">
			<ActionButton background="green" className="p-1 mx-10 my-5 w-24 flex flex-row justify-center items-center">
				<p>Export</p>
				<i className="material-icons">description</i>
			</ActionButton>
			<RequestList requests={requests} />
		</div>
	);
}

export default Users;
