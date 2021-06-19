import { useContext } from 'react';
import { ContentPage, ContentPageContext, Popup, Label, Input, Select, ActionButton, Form, DangerButton } from '../styled/Components';
import { AuthContext } from '../App';
import apiLink from '../API';
import { Request } from '../types/Common';
import { Table, TableHeaderRow, TableRow, TableRowData } from '../styled/Table';

/** 
* Displays the user list page
* @function RequestList
* */
const RequestList = (props: { requests: Request[], updateRequestList: () => void }) => {
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
				<RequestItem request={request} updateRequestList={props.updateRequestList} />
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
const RequestItem = (props: { request: Request, updateRequestList: () => void }) => {
	const authContext = useContext(AuthContext);
	const { request, updateRequestList } = props;

	let circleColor = "bg-gray-400";
	if (request.approved === true) {
		circleColor = "bg-green-700";
	} else if (request.approved === false) {
		circleColor = "bg-red-700";
	}

	const endDate = new Date(request.endDate);
	const startDate = new Date(request.startDate);
	const daysLeft = calculateDaysLeft(startDate, endDate);

	const deleteRequest = () => {
		fetch(`${apiLink}/user/${authContext.userId}/requests/${request.id}`, {
			method: 'delete',
			headers: {
				'Authorization': `Bearer ${authContext.jwtToken}`
			}
		})
			.then(res => res.json())
			.then(res => {
				if (res.status === "OK") {
					alert(res.message);
					updateRequestList();
				} else {
					alert("Sorry something went wrong!");
				}
			}).catch(err => console.log(err));
	}

	return (
		<TableRow>
			<TableRowData>{`${request.user.firstname} ${request.user.lastname}`}</TableRowData>
			<TableRowData>{new Date(request.createdOn).toDateString()}</TableRowData>
			<TableRowData>{new Date(request.startDate).toDateString()}</TableRowData>
			<TableRowData>{new Date(request.endDate).toDateString()}</TableRowData>
			<TableRowData className="flex flex-col justify-center items-center">
				<div className={`w-6 h-6 rounded-full ${circleColor} mr-3`}></div>
				{request.approved ? <p className="text-green-700">{daysLeft.toFixed(0)} days left</p> : null}
			</TableRowData>
			<TableRowData>
				<i onClick={deleteRequest}
					className="material-icons hover:bg-gray-200 
					hover:text-red-700 rounded-full p-2 cursor-pointer">delete</i>
			</TableRowData>
		</TableRow>
	);
}

const calculateDaysLeft = (start: Date, end: Date): number => {
	const nowMillis = Date.now();
	if (nowMillis >= end.getTime()) return 0;
	if (nowMillis <= start.getTime()) {
		const endToStartMillis = end.getTime() - start.getTime();
		return endToStartMillis / (1000 * 60 * 60 * 24);
	}

	const endToNowMillis = end.getTime() - Date.now();
	return endToNowMillis / (1000 * 60 * 60 * 24);
}



export default RequestList;
