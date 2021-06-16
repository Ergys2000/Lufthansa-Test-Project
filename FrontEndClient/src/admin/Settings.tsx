import { useContext, useEffect, useState } from 'react';
import { ContentPage, ContentPageContext, Popup, Label, Input, Select, ActionButton, Form, DangerButton } from '../styled/Components';
import { AuthContext } from '../App';
import { formatDate } from '../util/Utils';
import apiLink from '../API';
import { useRouteMatch, useParams, useHistory } from 'react-router-dom';
import { User } from '../types/Common';

/** 
* Displays the settings page
* @function Settings
* */
const Settings = (props: any) => {
	/* Get the authentication context and destructure elements */
	const authContext = useContext(AuthContext);
	const contentPageContext = useContext(ContentPageContext);
	const { url } = useRouteMatch();
	useEffect(() => {
		contentPageContext.setLocationList([{ title: "Update personal info", url: url }]);
	}, [authContext.userId]);
	return (
		<ContentPage>
			<div className="p-10">
				<PersonalInfo />
				<PasswordForm />
			</div>
		</ContentPage>
	);
}

/** 
* Displays and is able to modify personal infor 
* @function PersonalInfo
* @returns The personal info view
* */

const PersonalInfo = () => {
	/* Get the authentication context and destructure elements */
	const authContext = useContext(AuthContext);
	const history = useHistory();

	/* The state which will hold the form infomation */
	const [form, setForm] = useState<User>({
		id: 0,
		firstname: "",
		lastname: "",
		email: "",
		password: "",
		type: "admin",
		startDate: new Date(Date.now()).toString(),
		supervisor: null
	});
	useEffect(() => {
		const bearer = "Bearer " + authContext.jwtToken;
		fetch(`${apiLink}/admin/${authContext.userId}`, {
			headers: {
				"Authorization": bearer
			}
		})
			.then(res => res.json())
			.then(res => {
				if (res.status === "OK") {
					setForm(res.result);
				} else {
					alert(res.message);
				}
			}).catch(err => console.log(err));
	}, [authContext.userId]);

	const onChange = (event: React.ChangeEvent) => {
		event.preventDefault();
		const { name, value } = event.target as any;

		setForm({ ...form, [name]: value });
	}

	const onSubmit = async (event: React.FormEvent) => {
		event.preventDefault();
		const bearer = "Bearer " + authContext.jwtToken;
		await fetch(`${apiLink}/admin/${authContext.userId}`, {
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
					alert(res.message + ". Please relogin to confirm information!");
					authContext.signOut();
					history.push("/");
				} else {
					alert(res.message);
				}
			}).catch(err => console.log(err));
	}

	return (
		<div className="flex flex-col items-stretch min-w-min bg-gray-100 p-10 text-gray-800 rounded-xl shadow-xl">

			<div className="flex flex-row justify-center">
				<p className="font-bold flex-1 text-lg text-center">{`${form.firstname} ${form.lastname}`}</p>
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
					<p>Start date</p>
					<Input type="date" name="startDate" onChange={onChange} value={form.startDate}></Input>
				</Label>
				<ActionButton className="p-3 mt-3 w-40">Update</ActionButton>
			</Form>

		</div>
	);
}

/** 
* Displays the form that modifies the user password
* @function PasswordForm
* @returns Change password view 
* */
const PasswordForm = () => {
	const authContext = useContext(AuthContext);
	const history = useHistory();
	const [passwordForm , setPasswordForm] = useState({
		oldPassword: "",
		newPassword: "",
		confirmPassword: ""
	});

	const onChange = (event: React.ChangeEvent) => {
		event.preventDefault();
		const { name, value } = event.target as any;
		setPasswordForm({ ...passwordForm, [name]: value });
	}


	const onSubmit = async (event: React.FormEvent) => {
		event.preventDefault();
		const bearer = "Bearer " + authContext.jwtToken;
		await fetch(`${apiLink}/admin/${authContext.userId}/changepassword`, {
			method: "put",
			headers: {
				"Content-Type": "application/json",
				"Authorization": bearer
			},
			body: JSON.stringify(passwordForm)
		})
			.then(res => res.json())
			.then(res => {
				if (res.status === "OK") {
					alert(res.message + ". Please relogin to confirm information!");
					authContext.signOut();
					history.push("/");
				} else {
					alert(res.message);
				}
			}).catch(err => console.log(err));
	}


	return (
		<div className="mt-16 flex flex-col items-stretch min-w-min bg-gray-100 p-10 text-gray-800 rounded-xl shadow-xl">

			<div className="flex flex-row justify-center">
				<p className="font-bold flex-1 text-lg text-center">Change Password</p>
			</div>

			<Form onSubmit={onSubmit}>
				<Label>
					<p>Old password</p>
					<Input type="text" name="email" onChange={onChange} value={passwordForm.oldPassword}></Input>
				</Label>
				<Label>
					<p>New password</p>
					<Input type="text" name="email" onChange={onChange} value={passwordForm.newPassword}></Input>
				</Label>
				<Label>
					<p>Confirm password</p>
					<Input type="text" name="firstname" onChange={onChange} value={passwordForm.confirmPassword}></Input>
				</Label>
				<ActionButton className="p-3 mt-3 w-40">Update</ActionButton>
			</Form>

		</div>
	);
}


export default Settings;
