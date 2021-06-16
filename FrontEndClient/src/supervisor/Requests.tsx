import { useContext, useEffect, useState } from 'react';
import { ContentPage, ContentPageContext, Popup, Label, Input, Select, ActionButton, Form, DangerButton } from '../styled/Components';
import { AuthContext } from '../App';
import { formatDate } from '../util/Utils';
import apiLink from '../API';
import { useRouteMatch, useParams } from 'react-router-dom';
import { Request, User } from '../types/Common';
import RequestList from './RequestList';

/** 
* Displays the user list page
* @function RequestList
* */
const Requests = (props: any) => {
	const { userId } = useParams() as any;
	const authContext = useContext(AuthContext);
	const { url } = useRouteMatch();

	/* Set the location list in the header of the page */
	const contentPageContext = useContext(ContentPageContext);
	useEffect(() => {
		contentPageContext.setLocationList([{ title: "Request list", url: url }]);
	}, []);

	/* Set up the state that holds the requests, and also a value for updating the
	* list with a callback*/
	const [requests, setRequests] = useState<Request[]>([]);
	useEffect(() => {
		const bearer = "Bearer " + authContext.jwtToken;
		fetch(`${apiLink}/supervisor/${userId}/requests`, {
			headers: {
				'Authorization': bearer
			}
		})
			.then(res => res.json())
			.then(res => {
				if (res.status === "OK") {
					const usrs = res.result as Request[];
					setRequests(usrs.filter(usr => usr.id != authContext.userId));
				} else {
					alert(res.message);
				}
			}).catch(err => console.log(err));
	}, [userId]);

	return (
		<div className="flex flex-col">
			<RequestList requests={requests} />
		</div>
	);
}

export default Requests;
