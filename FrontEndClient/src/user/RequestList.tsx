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
const RequestList = (props: { requests: Request[], updateRequestList: () => void }) => {
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
				<RequestItem request={request} updateRequestList={props.updateRequestList} />
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
const RequestItem = (props: { request: Request, updateRequestList: () => void }) => {
	const authContext = useContext(AuthContext);
	const {request, updateRequestList} = props;

	let circleColor = "bg-gray-400";
	if (request.approved === true) {
		circleColor = "bg-green-700";
	} else if (request.approved === false) {
		circleColor = "bg-red-700";
	}

	const deleteRequest = () => {
		fetch(`${apiLink}/user/${authContext.userId}/requests/${request.id}`, {
			method: 'delete',
			headers: {
				'Authorization': `Bearer ${authContext.jwtToken}`
			}
		})
			.then(res => res.json())
			.then(res => {
				if(res.status === "OK") {
					alert(res.message);
					updateRequestList();
				} else {
					alert("Sorry something went wrong!");
				}
			}).catch(err => console.log(err));
	}
	return (
		<div className="flex flex-row justify-center bg-gray-100 w-full p-5 border-b border-gray-400 text-gray-700">
			<p className="flex-1 text-center">{`${request.user.firstname} ${request.user.lastname}`}</p>
			<p className="flex-1 text-center">{new Date(request.createdOn).toDateString()}</p>
			<p className="flex-1 text-center">{new Date(request.startDate).toDateString()}</p>
			<p className="flex-1 text-center">{new Date(request.endDate).toDateString()}</p>
			<div className="flex-1 flex justify-center items-center">
				<div className={`w-6 h-6 rounded-full ${circleColor} mr-3`}></div>
			</div>
			<div className="flex-1 flex justify-center items-center">
				<i onClick={deleteRequest}
					className="material-icons hover:bg-gray-200 
					hover:text-red-700 rounded-full p-2 cursor-pointer">delete</i>
			</div>
		</div>
	);
}



export default RequestList;
