import { useState } from 'react';
import { useHistory } from 'react-router-dom';
import { ActionButton, Input } from '../styled/Components';
import apiLink from '../API';

type ResetPasswordState = {
	stage: "email" | "code" | "password";
	data: StageData
};
type StageData = {
	email: string;
	code: string;
};

const ResetPassword = (props: any) => {
	const [stageState, setStageState] = useState<ResetPasswordState>({
		stage: "email",
		data: { email: "", code: "" }
	});
	const history = useHistory();

	function toCode(data: any): void {
		setStageState({ stage: "code", data: data });
	}
	function toPassword(data: any): void {
		setStageState({ stage: "password", data: data });
	}

	let element = <div>aklsdjfdj</div>;
	if (stageState.stage === "email") {
		element =
			<EmailStage data={stageState.data} nextStage={toCode} />
	} else if (stageState.stage === "code") {
		element =
			<CodeStage data={stageState.data} nextStage={toPassword} />
	} else if (stageState.stage === "password") {
		element = <NewPasswordStage data={stageState.data} />
	} else {
		history.push("/");
	}

	return element;
}

const EmailStage = (props: { data: StageData, nextStage: (data: any) => void }) => {
	const [email, setEmail] = useState("");
	const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		event.preventDefault();
		const value = event.target.value;
		setEmail(value);
	}
	const onClick = () => {
		fetch(`${apiLink}/resetpassword/${email}`)
			.then(res => res.json())
			.then(res => {
				if (res.status === "OK") {
					props.nextStage({ ...props.data, email: email });
				} else {
					alert(res.message);
				}
			}).catch(err => console.log(err));
	}
	return (
		<div className="flex flex-col justify-around items-center bg-gray-200 w-1/2 h-1/2 rounded-xl p-10">
			<p className="text-indigo-700 text-2xl border-b-2 border-gray-600 w-full">Enter your email</p>
			<Input type="text" onChange={onChange} className="my-5" name="email" placeholder="Email...">
			</Input>
			<ActionButton onClick={onClick} className="w-1/3 p-3">Continue</ActionButton>
		</div>
	);
}

const CodeStage = (props: { data: StageData, nextStage: (data: any) => void }) => {
	const [code, setCode] = useState("");
	const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		event.preventDefault();
		const value = event.target.value;
		setCode(value);
	}
	const onClick = async () => {
		if (code === "") return;
		await fetch(`${apiLink}/resetpassword/${props.data.email}/${code}`)
			.then(res => res.json())
			.then(res => {
				if (res.status === "OK") {
					props.nextStage({ ...props.data, code: code });
				} else {
					alert(res.message);
				}
			}).catch(err => console.log(err));
	}
	return (
		<div className="flex flex-col justify-around items-center bg-gray-200 w-1/2 h-1/2 rounded-xl p-10">
			<div className="text-indigo-700 border-b-2 border-gray-600 w-full">
				<p className="text-2xl">Enter your code</p>
				<p className="text-md text-gray-600">The code has been sent to your email</p>
			</div>
			<Input type="text" onChange={onChange} className="my-5" name="code" placeholder="Code...">
			</Input>
			<ActionButton onClick={onClick} className="w-1/3 p-3">Continue</ActionButton>
		</div>
	);
}

const NewPasswordStage = (props: { data: StageData }) => {
	const history = useHistory();
	const [password, setPassword] = useState("");
	const onChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		event.preventDefault();
		const value = event.target.value;
		setPassword(value);
	}
	async function onClick() {
		const data = {
			password: password
		};
		await fetch(`${apiLink}/resetpassword/${props.data.email}/${props.data.code}`, {
			method: "put",
			headers: {
				"Content-Type": "application/json"
			},
			body: JSON.stringify(data)
		})
			.then(res => res.json())
			.then(res => {
				if (res.status === "OK") {
					alert("Password changed successfully!");
					history.push("/");
				} else {
					alert(res.message);
				}
			}).catch(err => console.log(err));
	}
	return (
		<div className="flex flex-col justify-around items-center bg-gray-200 w-1/2 h-1/2 rounded-xl p-10">
			<p className="text-indigo-700 text-2xl border-b-2 border-gray-600 w-full">Enter your new password</p>
			<Input type="password" onChange={onChange} className="my-5" name="password" placeholder="Password...">
			</Input>
			<ActionButton className="w-1/3 p-3" onClick={onClick}>Update</ActionButton>
		</div>
	);
}

export default ResetPassword;
