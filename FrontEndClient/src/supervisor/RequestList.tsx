import { useState, useContext } from 'react';
import { AuthContext } from '../App';
import apiLink from '../API';
import { Request  } from '../types/Common';
import { Table, TableHeaderRow, TableRow, TableRowData } from '../styled/Table';

/** 
* Displays the user list page
* @function RequestList
* */
const RequestList = (props: { requests: Request[] }) => {
	const { requests } = props;
	return (
		<Table>
			<TableHeaderRow>
				<TableRowData>User</TableRowData>
				<TableRowData>Created on</TableRowData>
				<TableRowData>Start date</TableRowData>
				<TableRowData>End date</TableRowData>
				<TableRowData>Status</TableRowData>
				<TableRowData>Actions</TableRowData>
			</TableHeaderRow>
			{requests.map(request => (
				<RequestItem key={request.id} request={request} />
			))}
		</Table>
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
		<TableRow>
			<TableRowData>{`${request.user.firstname} ${request.user.lastname}`}</TableRowData>
			<TableRowData>{new Date(request.createdOn).toDateString()}</TableRowData>
			<TableRowData>{new Date(request.startDate).toDateString()}</TableRowData>
			<TableRowData>{new Date(request.endDate).toDateString()}</TableRowData>
			<TableRowData className="flex flex-row justify-center items-center">
				<div className={`w-6 h-6 rounded-full ${circleColor} mr-3`}></div>
			</TableRowData>
			<TableRowData className="flex flex-row justify-around items-center">
				<button className="focus:outline-none flex justify-center 
					items-center bg-transparent text-green-700 hover:bg-gray-300 rounded-full p-1">
					<i onClick={() => onIconClicked(true)} className="material-icons hover:text-green-700 hover:cursor-pointer">done</i>
				</button>
				<button className="focus:outline-none flex justify-center 
					items-center bg-transparent text-red-700 hover:bg-gray-300 rounded-full p-1">
					<i onClick={() => onIconClicked(false)} className="material-icons hover:text-red-700 hover:cursor-pointer">cancel</i>
				</button>
			</TableRowData>
		</TableRow>
	);
}



export default RequestList;
