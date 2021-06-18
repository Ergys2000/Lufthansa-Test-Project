import { useContext, useEffect, useState } from 'react';
import { ContentPage, ContentPageContext, Popup, Label, Input, Select, ActionButton, Form, DangerButton } from '../styled/Components';
import { AuthContext } from '../App';
import { formatDate } from '../util/Utils';
import apiLink from '../API';
import { useRouteMatch, useParams } from 'react-router-dom';
import { User } from '../types/Common';
import { Table, TableHeaderRow, TableRow, TableRowData } from '../styled/Table';

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
	const [shouldUpdate, setShouldUpdate] = useState(0);
	useEffect(() => {
		const bearer = "Bearer " + authContext.jwtToken;
		fetch(`${apiLink}/admin/${userId}/users`, {
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
	}, [userId, shouldUpdate]);

	/* Determines whether the add user form is displayed*/
	const [addUser, setAddUser] = useState(false);

	/* Create a list of supervisors from the current users */
	const supervisors = users.filter(user => user.type === "supervisor");

	/* The callback which updates the list */
	const updateList = () => {
		setShouldUpdate(shouldUpdate + 1);
	}
	return (
		<div className="flex flex-col">
			<ActionButton onClick={() => setAddUser(true)} background="green" className="p-1 mx-10 my-5 w-14">
				<i className="material-icons">add</i>
			</ActionButton>

			{addUser ? <AddUserForm
				update={updateList}
				supervisors={supervisors}
				close={() => setAddUser(false)} /> : null}

			<Table>
				<TableHeaderRow>
					<TableRowData>Type</TableRowData>
					<TableRowData>Email</TableRowData>
					<TableRowData>Firstname</TableRowData>
					<TableRowData>Lastname</TableRowData>
					<TableRowData>Supervisor</TableRowData>
				</TableHeaderRow>
				{users.map(user => <UserItem key={user.id}
					update={updateList}
					user={user}
					supervisors={supervisors} />)}
			</Table>
		</div>
	);
}

/** 
* Displays a user list item
* @function UserItem
* @param {Object} props - The props of the component
* @param {User} props.user - The user this item represents
* @param {User[]} props.supervisors - The list of available supervisors
* @param {()=>void} props.update - Callback for updating the main user list
* @returns A single user item for the user list
* */
const UserItem = (props: { user: User, supervisors: User[], update: () => void }) => {
	const { user, supervisors } = props;

	/* The state which determines whether the popup window to modify a user is
	* shown */
	const [popupShown, setPopupShown] = useState(false);
	return (
		<>
			<TableRow className="hover:bg-gray-300" onClick={() => setPopupShown(true)}>
				<TableRowData>{user.type}</TableRowData>
				<TableRowData>{user.email}</TableRowData>
				<TableRowData>{user.firstname}</TableRowData>
				<TableRowData>{user.lastname}</TableRowData>
				<TableRowData>{user.supervisor?.firstname}</TableRowData>
			</TableRow>
			{popupShown ? <UserViewForm
				user={user}
				update={props.update}
				close={() => setPopupShown(false)}
				supervisors={supervisors} /> : null}
		</>
	);
}

/** 
* Displays the form to modify a user
* @function UserViewForm
* @param {Object} props - The props of the component
* @param {User} props.user - The user this item represents
* @param {User[]} props.supervisors - The list of available supervisors
* @param {()=>void} props.update - Callback for updating the main user list
* @param {()=>void} props.close - Callback for closing the updat form
* @returns A popup window which displays the user
* */
const UserViewForm = (props: {
	supervisors: User[],
	user: User,
	update: () => void,
	close: () => void
}) => {
	/* Get the authentication context and destructure elements */
	const authContext = useContext(AuthContext);
	const { close, supervisors } = props;
	let { user } = props;

	/* The state which will hold the form infomation */
	const [form, setForm] = useState<User>({ ...user });

	const onChange = (event: React.ChangeEvent) => {
		event.preventDefault();
		const { name, value } = event.target as any;

		if (name === "type") {
			if (value === "admin" || value === "supervisor") {
				setForm({ ...form, [name]: value, supervisor: null });
				return;
			}
		}

		setForm({ ...form, [name]: value });
	}

	const changeSupervisor = async (event: React.ChangeEvent) => {
		event.preventDefault();
		const { name, value } = event.target as any;
		if (value == "0") {
			setForm({ ...form, supervisor: null });
			return;
		}

		for (let i = 0; i < supervisors.length; i++) {
			if (supervisors[i].id == value) {
				setForm({ ...form, supervisor: supervisors[i] });
			}
		}
	}

	const onSubmit = async (event: React.FormEvent) => {
		event.preventDefault();
		const bearer = "Bearer " + authContext.jwtToken;
		await fetch(`${apiLink}/admin/${authContext.userId}/users/${user.id}`, {
			method: "put",
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
					for (const property in user) {
						(user as any)[property] = (form as any)[property];
					}
					close();
				} else {
					console.log(res);
					alert(res.message);
				}
			}).catch(err => console.log(err));
	}

	const onDelete = async (event: React.FormEvent) => {
		event.preventDefault();
		const bearer = "Bearer " + authContext.jwtToken;
		await fetch(`${apiLink}/admin/${authContext.userId}/users/${user.id}`, {
			method: "delete",
			headers: {
				"Authorization": bearer
			}
		})
			.then(res => res.json())
			.then(res => {
				if (res.status === "OK") {
					alert(res.message);
					props.update();
					close();
				} else {
					console.log(res);
					alert(res.message);
				}
			}).catch(err => console.log(err));
	}

	return (
		<Popup>
			<div className="flex flex-col items-stretch w-1/2 min-w-min min-h-screen m-auto bg-gray-200 p-5 text-gray-800">

				<div className="flex flex-row justify-center">
					<p className="font-bold flex-1 text-lg text-center">{`${user.firstname} ${user.lastname}`}</p>
					<i onClick={props.close} className="material-icons cursor-pointer text-gray-600 hover:text-gray-800">close</i>
				</div>

				<Form onSubmit={onSubmit}>
					<Label>
						<p>Email</p>
						<Input type="text" name="email" onChange={onChange} value={form.email}></Input>
					</Label>
					<Label>
						<p>Firstname</p>
						<Input type="text" name="firstname" onChange={onChange} value={form.firstname}></Input>
					</Label>
					<Label>
						<p>Lastname</p>
						<Input type="text" name="lastname" onChange={onChange} value={form.lastname}></Input>
					</Label>
					<Label>
						<p>Type</p>
						<Select
							name="type" onChange={onChange}
							value={form.type}>
							<option value={"user"}>User</option>
							<option value={"supervisor"}>Supervisor</option>
							<option value={"admin"}>Admin</option>
						</Select>
					</Label>
					<Label>
						<p>Start date</p>
						<Input type="date" name="startDate" onChange={onChange} value={form.startDate}></Input>
					</Label>
					<Label>
						<p>Supervisor</p>
						<Select
							disabled={form.type === "supervisor" || form.type === "admin"}
							name="supervisor" onChange={changeSupervisor}
							value={form.supervisor == null ? 0 : form.supervisor.id}>

							<option value={0}>{"No supervisor"}</option>
							{supervisors.map(supervisor =>
								<option
									key={supervisor.id}
									value={supervisor.id}>{`${supervisor.firstname} ${supervisor.lastname}`}</option>)
							}
						</Select>
					</Label>
					<ActionButton className="p-3 mt-3 w-40">Update</ActionButton>
					<DangerButton onClick={onDelete} className="p-3 mt-3 w-40">Delete</DangerButton>
				</Form>

			</div>
		</Popup>
	);
}

/** 
* Displays the form to add a user
* @function UserViewForm
* @param {Object} props - The props of the component
* @param {User[]} props.supervisors - The list of available supervisors
* @param {()=>void} props.update - Callback for updating the main user list
* @param {()=>void} props.close - Callback for closing the updat form
* @returns A popup window which displays the user
* */
const AddUserForm = (props: {
	supervisors: User[],
	update: () => void,
	close: () => void
}) => {
	/* Get the authentication context and destructure elements */
	const authContext = useContext(AuthContext);
	const { close, supervisors } = props;

	/* The state which will hold the information about the form */
	const [form, setForm] = useState<User>({
		id: 0,
		firstname: "",
		lastname: "",
		password: "",
		email: "",
		type: "user",
		supervisor: null,
		startDate: new Date(Date.now()).toUTCString()
	});

	const onChange = (event: React.ChangeEvent) => {
		event.preventDefault();
		const { name, value } = event.target as any;

		if (name === "type") {
			if (value === "admin" || value === "supervisor") {
				setForm({ ...form, [name]: value, supervisor: null });
				return;
			}
		}
		setForm({ ...form, [name]: value });
	}

	const changeSupervisor = async (event: React.ChangeEvent) => {
		event.preventDefault();
		const { name, value } = event.target as any;
		if (value == "0") {
			setForm({ ...form, supervisor: null });
			return;
		}

		for (let i = 0; i < supervisors.length; i++) {
			if (supervisors[i].id == value) {
				setForm({ ...form, supervisor: supervisors[i] });
			}
		}
	}

	const onSubmit = async (event: React.FormEvent) => {
		event.preventDefault();
		const bearer = "Bearer " + authContext.jwtToken;
		await fetch(`${apiLink}/admin/${authContext.userId}/users`, {
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
					props.update();
					close();
				} else {
					console.log(res);
					alert(res.message);
				}
			}).catch(err => console.log(err));
	}


	return (
		<Popup>
			<div className="flex flex-col items-stretch w-1/2 min-w-min min-h-screen m-auto bg-gray-200 p-5 text-gray-800">

				<div className="flex flex-row justify-center">
					<p className="font-bold flex-1 text-lg text-center">Add User</p>
					<i onClick={props.close} className="material-icons cursor-pointer text-gray-600 hover:text-gray-800">close</i>
				</div>

				<Form onSubmit={onSubmit}>
					<Label>
						<p>Email</p>
						<Input type="text" name="email" onChange={onChange} value={form.email}></Input>
					</Label>
					<Label>
						<p>Password</p>
						<Input type="text" name="password" onChange={onChange} value={form.password}></Input>
					</Label>
					<Label>
						<p>Firstname</p>
						<Input type="text" name="firstname" onChange={onChange} value={form.firstname}></Input>
					</Label>
					<Label>
						<p>Lastname</p>
						<Input type="text" name="lastname" onChange={onChange} value={form.lastname}></Input>
					</Label>
					<Label>
						<p>Type</p>
						<Select
							name="type" onChange={onChange}
							value={form.type}>
							<option value={"user"}>User</option>
							<option value={"supervisor"}>Supervisor</option>
							<option value={"admin"}>Admin</option>
						</Select>
					</Label>
					<Label>
						<p>Start date</p>
						<Input type="date" name="startDate" onChange={onChange} value={form.startDate}></Input>
					</Label>
					<Label>
						<p>Supervisor</p>
						<Select
							disabled={form.type === "supervisor" || form.type === "admin"}
							name="supervisor" onChange={changeSupervisor}
							value={form.supervisor == null ? 0 : form.supervisor.id}>

							<option value={0}>{"No supervisor"}</option>
							{supervisors.map(supervisor =>
								<option
									key={supervisor.id}
									value={supervisor.id}>{`${supervisor.firstname} ${supervisor.lastname}`}</option>)
							}
						</Select>
					</Label>
					<ActionButton className="p-3 mt-3 w-40">Add</ActionButton>
				</Form>

			</div>
		</Popup>
	);
}

export default UserList;
