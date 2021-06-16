import { useContext, useEffect, useState } from 'react';
import { ContentPage, ContentPageContext, Popup, Label, Input, Select, ActionButton, Form, DangerButton } from '../styled/Components';
import { AuthContext } from '../App';
import { formatDate } from '../util/Utils';
import apiLink from '../API';
import { useRouteMatch, useParams } from 'react-router-dom';
import { Request, User } from '../types/Common';

/** 
* Displays the user list page
* @function RequestList
* */
const RequestList = (props: { requests: Request[] }) => {
	const { requests } = props;
	return (
		<div className="mx-10 border-l-2 border-r-2 border-b-2 border-gray-800 shadow-sm">
			<div className="bg-gray-800 flex flex-row justify-center w-full text-gray-300 p-2">
				<p className="font-bold flex-1 text-center">User</p>
				<p className="font-bold flex-1 text-center">Created on</p>
				<p className="font-bold flex-1 text-center">Start date</p>
				<p className="font-bold flex-1 text-center">End date</p>
				<p className="font-bold flex-1 text-center">Status</p>
				<p className="font-bold flex-1 text-center">Actions</p>
			</div>
			{requests.map(request => (
				<RequestItem request={request} />
			))}
		</div>
	);
}

/** 
* Displays a request list item
* @function RequestItem
* @param {Object} props - The props of the component
* @param {Request} props.request - The request this item represents
* @param {()=>void} props.update - Callback for updating the main list
* @returns A single request item for the request list
* */
const RequestItem = (props: { request: Request }) => {
	const authContext = useContext(AuthContext);
	const [request, setRequest] = useState(props.request);

	/* The state which determines whether the popup window to modify a user is
	* shown */
	const [popupShown, setPopupShown] = useState(false);

	const onIconClicked = async (value: boolean) => {
		const bearer = "Bearer " + authContext.jwtToken;
		await fetch(`${apiLink}/supervisor/${authContext.userId}/requests/${request.id}?approved=${value}`, {
			method: "put",
			headers: {
				"Authorization": bearer
			}
		})
			.then(res => res.json())
			.then(res => {
				if (res.status === "OK") {
					alert(res.message);
					setRequest({ ...request, approved: value });
				} else {
					alert("Sorry something went wrong!");
				}
			}).catch(err => console.log(err));
	}
	let circleColor = "bg-gray-400";
	if (request.approved === true) {
		circleColor = "bg-green-700";
	} else if (request.approved === false) {
		circleColor = "bg-red-700";
	}
	return (
		<div onClick={() => setPopupShown(true)}
			className="flex flex-row justify-center bg-gray-100 w-full p-5 border-b border-gray-400 text-gray-700">
			<p className="flex-1 text-center">{`${request.user.firstname} ${request.user.lastname}`}</p>
			<p className="flex-1 text-center">{new Date(request.createdOn).toDateString()}</p>
			<p className="flex-1 text-center">{new Date(request.startDate).toDateString()}</p>
			<p className="flex-1 text-center">{new Date(request.endDate).toDateString()}</p>
			<div className="flex-1 flex justify-center items-center">
				<div className={`w-6 h-6 rounded-full ${circleColor} mr-3`}></div>
			</div>
			<div className="flex-1 flex flex-row justify-around text-center">
				<button className="focus:outline-none flex justify-center 
					items-center bg-transparent text-green-700 hover:bg-gray-300 rounded-full p-1">
					<i onClick={() => onIconClicked(true)} className="material-icons hover:text-green-700 hover:cursor-pointer">done</i>
				</button>
				<button className="focus:outline-none flex justify-center 
					items-center bg-transparent text-red-700 hover:bg-gray-300 rounded-full p-1">
					<i onClick={() => onIconClicked(false)} className="material-icons hover:text-red-700 hover:cursor-pointer">cancel</i>
				</button>
			</div>
		</div>
	);
}



export default RequestList;
