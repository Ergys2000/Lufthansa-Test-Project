import React, { useContext, useState } from 'react';
import {
	useHistory,
	Link,
} from 'react-router-dom';
import { AuthContext } from '../App';
import apiLink from '../API';
import { Input, ActionButton, Form, Label } from '../styled/Components';

const Login = () => {
	const history = useHistory();
	const authContext = useContext(AuthContext);

	const [form, setForm] = useState({
		username: "",
		password: ""
	});

	const onSubmit = async (event: React.FormEvent) => {
		event.preventDefault();
		await fetch(`${apiLink}/authenticate`,{
			method: "post",
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(form)
		})
			.then(res => res.json())
			.then(res => {
				if(res.status === "OK") {
					console.log(res);
					authContext.signIn(res.result.id, res.result.jwt);
					history.push(`/${res.result.type}/${res.result.id}`);
				} else {
					alert(res.message);
				}
			}).catch(err => console.log(err));
	}

	const onChange = (event: React.ChangeEvent) => {
		event.preventDefault();
		const { name, value } = event.target as any;
		setForm({ ...form, [name]: value });
	}

	return (
		<div className="bg-indigo-900 m-auto w-9/12 rounded-lg px-10 pt-10 text-center min-w-max max-w-4xl shadow-lg text-gray-300">
			<p className="text-2xl"><b>Issue Tracker Login</b></p>
			<Form>
				<Label>
					Email:
					<Input
						name="username"
						value={form.username}
						onChange={onChange}
						type="text" />
				</Label>
				<Label>
					Password:
					<Input
						name="password"
						value={form.password}
						onChange={onChange}
						type="password" />
				</Label>
				<Link to="/reset" className="mr-auto">Forgot password?</Link>
				<ActionButton className={"px-3 py-2 m-3"}
					onClick={onSubmit}>
					Log in
				</ActionButton>
			</Form>
		</div>
	);
}

export default Login;
