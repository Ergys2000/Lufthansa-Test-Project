import { useState } from 'react';
import { useHistory } from 'react-router-dom';
import {ActionButton} from '../styled/Components';

const ResetPassword = (props: any) => {
	const [stage, setStage] = useState("email");
	const history = useHistory();

	let element = <div>lkjasdkljf</div>;
	if (stage === "email") {
		element = <EmailStage nextStage={() => setStage("code")} />
	} else if (stage === "code") {
		element = <Code nextStage={() => setStage("password")} />
	} else if (stage === "password") {
		element = <NewPassword nextStage={() => setStage("done")} />
	} else {
		history.push("/");
	}

	return element;
}

const EmailStage = (props: {nextStage: () => void}) => {
	return (
		<div className="flex flex-col bg-gray-200">
			Enter email
			<ActionButton onClick={props.nextStage}>Go to next stage</ActionButton>
		</div>
	);
}

const Code = (props: {nextStage: () => void}) => {
	return (
		<div className="flex flex-col bg-gray-200">
			Enter code
			<ActionButton onClick={props.nextStage}>Go to next stage</ActionButton>
		</div>
	);
}

const NewPassword = (props: {nextStage: () => void}) => {
	return (
		<div className="flex flex-col bg-gray-200">
			Enter new password
			<ActionButton onClick={props.nextStage}>Go to next stage</ActionButton>
		</div>
	);
}

export default ResetPassword;
