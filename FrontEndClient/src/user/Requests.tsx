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

	/* The user object which will be used to set the user of a request, the 
	* backend only needs the id to identify it */
	const user = {
		id: userId
	};

	/* Set the location list in the header of the page */
	const contentPageContext = useContext(ContentPageContext);
	useEffect(() => {
		contentPageContext.setLocationList([{ title: "Requests", url: url }]);
	}, []);

	/* Set up the state that holds the requests, and also a value for updating the
	* list with a callback*/
	const [requests, setRequests] = useState<Request[]>([]);
	const [update, setUpdate] = useState(0);
	const updateRequestList = () => setUpdate(update + 1);
	useEffect(() => {
		const bearer = "Bearer " + authContext.jwtToken;
		fetch(`${apiLink}/user/${userId}/requests`, {
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
	}, [userId, update]);

	const [addRequest, setAddRequest] = useState(false);

	return (
		<div className="flex flex-col">
			<ActionButton onClick={() => setAddRequest(true)} background="green" className="p-1 mx-10 my-5 w-14">
				<i className="material-icons">add</i>
			</ActionButton>
			<RequestList requests={requests} updateRequestList={updateRequestList} />
			{addRequest ? <AddRequest user={user as User} close={() => setAddRequest(false)} update={updateRequestList} /> : null}
		</div>
	);
}

const AddRequest = (props: { user: User, close: () => void, update: () => void }) => {
	/* Get the authentication context and destructure elements */
	const authContext = useContext(AuthContext);
	const { user, close, update } = props;

	/* The state which will hold the information about the form */
	const [form, setForm] = useState<Request>({
		id: 0,
		startDate: "",
		endDate: "",
		createdOn: "",
		approved: null,
		user: user
	});

	const onChange = (event: React.ChangeEvent) => {
		event.preventDefault();
		const { name, value } = event.target as any;
		setForm({ ...form, [name]: value });
	}

	const onSubmit = async (event: React.FormEvent) => {
		event.preventDefault();
		const bearer = "Bearer " + authContext.jwtToken;
		await fetch(`${apiLink}/user/${authContext.userId}/requests`, {
			method: "post",
			headers: {
				"Content-Type": "application/json",
				"Authorization": bearer
			},
			body: JSON.stringify(form)
		})
			.then(res => res.json())
			.then(res => {
				if (res.status === "OK") {
					alert(res.message);
					update();
					close();
				} else {
					alert(res.message);
				}
			}).catch(err => console.log(err));
	}


	return (
		<Popup>
			<div className="flex flex-col items-stretch w-1/2 min-w-min min-h-screen m-auto bg-gray-200 p-5 text-gray-800">

				<div className="flex flex-row justify-center">
					<p className="font-bold flex-1 text-lg text-center">Add Request</p>
					<i onClick={props.close} className="material-icons cursor-pointer text-gray-600 hover:text-gray-800">close</i>
				</div>

				<Form onSubmit={onSubmit}>
					<Label>
						<p>Start date</p>
						<Input type="date" name="startDate" onChange={onChange} value={form.startDate}></Input>
					</Label>
					<Label>
						<p>End date</p>
						<Input type="date" name="endDate" onChange={onChange} value={form.endDate}></Input>
					</Label>
					<ActionButton className="p-3 mt-3 w-40">Add</ActionButton>
				</Form>

			</div>
		</Popup>
	);
}

export default Requests;
