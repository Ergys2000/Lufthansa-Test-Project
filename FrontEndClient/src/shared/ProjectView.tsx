import { useEffect, useContext, useState } from 'react';
import { ContentPageContext } from '../styled/Components';
import { Link, useRouteMatch, Switch, Route, Redirect } from 'react-router-dom';
import { Issue, Comment } from '../types/Common';
import { Popup, ActionButton } from '../styled/Components';
import { AuthContext } from '../App';

/** Utility function for converting a date into the correct format for the date
* input element
* @function toDateInputString
* @param {Date} date
* @returns {string} the string in the correct format
* */
const toDateInputString = (date: Date): string => {
	let isoString = date.toISOString();
	return isoString.split(".")[0];
}

/** Renders the page for the issues of a project
* @function ProjectView
* @returns The project view for a single project 
* */
const ProjectView = () => {
	const { url, path } = useRouteMatch();
	const contentPageContext = useContext(ContentPageContext);
	useEffect(() => {
		const locationList = contentPageContext.getLocationList();
		locationList.push({ title: "Project", url: url });
		contentPageContext.setLocationList(locationList);
	}, []);

	return (
		<>
			<HorizontalNavBar />
			<div className="overflow-auto">
				<Switch>
					<Route path={`${path}/all`}>
						<AllIssuesView />
					</Route>
					<Route path={`${path}/open`}>
						<OpenIssuesView />
					</Route>
					<Route path={`${path}/closed`}>
						<ClosedIssuesView />
					</Route>
					<Route path={`${path}/mine`}>
						<MyIssuesView />
					</Route>
					<Route path={`${path}/`}>
						<Redirect to={`${url}/all`} />
					</Route>
				</Switch>
			</div>
		</>
	);
}

/** Renders the horizontal nav bar for the project view page
* @function HorizontalNavBar
* @returns Horizontal Nav bar component page
* */
const HorizontalNavBar = () => {
	const { url } = useRouteMatch();
	return (
		<nav className={`group flex flex-row justify-start bg-gray-900 mx-10 h-12 duration-300`}>
			<Link
				to={`${url}/all`}
				className={`flex justify-center items-center text-gray-200 w-20 h-full hover:bg-purple-700`}
			>All</Link>
			<Link
				to={`${url}/open`}
				className={`flex justify-center items-center text-gray-200 w-20 h-full hover:bg-purple-700`}
			>Open</Link>
			<Link
				to={`${url}/closed`}
				className={`flex justify-center items-center text-gray-200 w-20 h-full hover:bg-purple-700`}
			>Closed</Link>
			<Link
				to={`${url}/mine`}
				className={`flex justify-center items-center text-gray-200 w-20 h-full hover:bg-purple-700`}
			>Mine</Link>
		</nav>
	);
}

/** Displays all the issues for a particular project
* @function AllIssuesView
* @returns The list of all issues page
* */
const AllIssuesView = () => {
	const [issues, setIssues] = useState<Issue[]>([]);
	return (
		<>
			<IssueList issues={issues} />
		</>
	);
}

/** Displays all the open issues for a particular project
* @function OpenIssuesView
* @returns The list of open issues page
* */
const OpenIssuesView = () => {
	const [issues, setIssues] = useState<Issue[]>([]);
	return (
		<>
			<IssueList issues={issues} />
		</>
	);
}

/** Displays all the closed issues for a particular project
* @function ClosedIssuesView
* @returns The list of closed issues page
* */
const ClosedIssuesView = () => {
	const [issues, setIssues] = useState<Issue[]>([]);
	return (
		<>
			<IssueList issues={issues} />
		</>
	);
}

/** Displays the user issues for a particular project
* @function MyIssuesView
* @returns The list of user issues page
* */
const MyIssuesView = () => {
	const [issues, setIssues] = useState<Issue[]>([]);
	return (
		<>
			<IssueList issues={issues} />
		</>
	);
}

/** Renders a list of issues
* @function IssueList
* @param props {Object} The component props
* @param props.issues {Issue[]} The list of issues
* @returns The list of issues
* */
const IssueList = (props: { issues: Issue[] }) => {
	return (
		<div className="flex flex-col m-10 bg-gray-300">

			<div className="flex flex-row justify-evenly items-center bg-gray-800 text-gray-200 h-8">
				<p className="font-bold flex-1 text-center">Title</p>
				<p className="font-bold flex-1 text-center">Description</p>
				<p className="font-bold flex-1 text-center">Status</p>
				<p className="font-bold flex-1 text-center">Reporter Id</p>
				<p className="font-bold flex-1 text-center">Asignee Id</p>
				<p className="font-bold flex-1 text-center">Priority</p>
				<p className="font-bold flex-1 text-center">Created On</p>
				<p className="font-bold flex-1 text-center">Deadline</p>
			</div>

			{props.issues.map(issue => <IssueItem issue={issue} />)}
		</div>
	);
}

/** Renders a single issue for the list
* @function IssueItem
* @param props {Object} The component props
* @param props.issue {Issue} The issue to render
* @returns The issue item for the list
* */
const IssueItem = (props: { issue: Issue }) => {
	const { issue } = props;

	/* If the title or description are too long cut them */
	let title = issue.title;
	if (issue.title.length > 20) {
		title = `${issue.title.slice(0, 20)}...`;
	}
	let description = issue.description;
	if (issue.description.length > 20) {
		description = `${issue.description.slice(0, 20)}...`;
	}

	/* Determine the color of the priority element */
	let priorityColor = "text-green-600";
	if (issue.priority === "medium") {
		priorityColor = "text-yellow-600";
	} else if (issue.priority === "high") {
		priorityColor = "text-red-600";
	}

	const [popup, setPopup] = useState(false);

	return (
		<>
			<div onClick={() => setPopup(true)} className="flex flex-row h-16 justify-evenly items-center even:bg-gray-200 hover:bg-gray-400">
				<p className="flex-1 text-center">{title}</p>
				<p className="flex-1 text-center">{description}</p>
				<p className="flex-1 text-center">{issue.status}</p>
				<p className="flex-1 text-center">{issue.reporterId}</p>
				<p className="flex-1 text-center">{issue.asigneeId}</p>
				<p className={`flex-1 text-center ${priorityColor}`}>{issue.priority}</p>
				<div className="flex-1 text-center">
					<p>{issue.createdOn.toDateString()}</p>
					<p>{issue.createdOn.toLocaleTimeString()}</p>
				</div>
				<div className="flex-1 text-center">
					<p>{issue.deadline.toDateString()}</p>
					<p>{issue.deadline.toLocaleTimeString()}</p>
				</div>
			</div>
			{popup
				? <IssueView close={() => setPopup(false)} issue={issue} />
				: null}
		</>
	);
}

/** Renders a single issue in a popup view 
* @function IssueView
* @param props {Object} The component props
* @param props.issue {Issue} The issue to render
* @param props.close {() => void} The function to close the popup
* @returns The issue view in a pop up
* */
const IssueView = (props: { close: () => void, issue: Issue }) => {
	const { issue } = props;
	return (
		<Popup>
			<div className="issue-card-grid gap-2 w-1/2 min-w-min min-h-screen m-auto bg-gray-200 p-5 text-gray-800">

				<div className="flex flex-row justify-center col-start-1 col-end-4">
					<p className="font-bold flex-1 text-lg text-center">{issue.title}</p>
					<i onClick={props.close} className="material-icons cursor-pointer text-gray-600 hover:text-gray-800">close</i>
				</div>

				<Description issue={issue} />

				<Status issue={issue} />

				<Activity issue={issue} />

			</div>
		</Popup>
	);
}

/** Renders the description of an issue 
* @function Description
* @param props {Object} The component props
* @param props.issue {Issue} The issue to render
* @returns The issue description view
* */
const Description = (props: { issue: Issue }) => {
	const { issue } = props;
	const authContext = useContext(AuthContext);

	const [description, setDescription] = useState(issue.description);
	const [changed, setChanged] = useState(false);

	const onChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
		event.preventDefault();
		setDescription(event.target.value);
		setChanged(true);
	}
	const onSave = (event: React.MouseEvent<HTMLButtonElement>) => {
		event.preventDefault();
	}
	const onCancel = (event: React.MouseEvent<HTMLElement>) => {
		event.preventDefault();
		setDescription(issue.description);
		setChanged(false);
	}

	return (
		<div className="col-start-1 col-end-3 z-20">
			<p className="text-2xl font-bold">Description</p>
			<p className="my-2"><b>By:</b> {issue.reporter}</p>
			<p className="my-2"><b>Assigned to:</b> {issue.asignee}</p>
			<textarea name="description" onChange={onChange} className="w-full min-h-40 bg-gray-300 p-2" value={description}></textarea>
			{changed
				? <div className="flex flex-row items-center">
					<ActionButton onClick={onSave} className="p-2">Save</ActionButton>
					<i className="material-icons cursor-pointer text-gray-600 hover:text-gray-800" onClick={onCancel}>close</i>
				</div>
				: null
			}
		</div>
	);
}

/** Renders the status of an issue 
* @function Status
* @param props {Object} The component props
* @param props.issue {Issue} The issue to render
* @returns The issue status view
* */
const Status = (props: { issue: Issue }) => {
	const { issue } = props;
	const authContext = useContext(AuthContext);

	const [state, setState] = useState({
		priority: issue.priority,
		status: issue.status,
		deadline: toDateInputString(issue.deadline),
		createdOn: toDateInputString(issue.createdOn),
	});
	const [changed, setChanged] = useState(false);

	const onChange = (event: React.ChangeEvent) => {
		event.preventDefault();
		const { name, value } = event.target as any;
		setState({ ...state, [name]: value });
		setChanged(true);
	}

	const onSave = (event: React.MouseEvent<HTMLButtonElement>) => {
		event.preventDefault();
	}

	const onCancel = (event: React.MouseEvent<HTMLButtonElement>) => {
		event.preventDefault();
		setState({
			status: issue.status,
			priority: issue.priority,
			createdOn: toDateInputString(issue.createdOn),
			deadline: toDateInputString(issue.deadline),
		});
		setChanged(false);
	}

	return (
		<div className="flex flex-col justify-between items-stretch col-start-3 col-end-4">
			<p className="font-bold text-2xl">State</p>

			<label className="flex flex-col jusfity-start items-stretch text-lg">
				<p className="text-center">Priority</p>
				<select name="priority" onChange={onChange} value={state.priority}>
					<option value="low">Low</option>
					<option value="medium">Medium</option>
					<option value="high">High</option>
				</select>
			</label>

			<label className="flex flex-col jusfity-start items-stretch text-lg">
				<p className="text-center">Status</p>
				<select name="status" onChange={onChange} value={state.status}>
					<option value="open">Open</option>
					<option value="closed">Closed</option>
					<option value="resolved">Resolved</option>
				</select>
			</label>

			<label className="flex flex-col jusfity-start items-stretch text-lg">
				<p className="text-center">Deadline</p>
				<input name="deadline" onChange={onChange} value={state.deadline} type="datetime-local" />
			</label>

			<label className="flex flex-col jusfity-start items-stretch text-lg">
				<p className="text-center">Created on</p>
				<input name="createdOn" onChange={onChange} value={state.createdOn} type="datetime-local" />
			</label>

			{changed
				? <div className="flex flex-row items-center">
					<ActionButton onClick={onSave} className="p-2">Save</ActionButton>
					<i className="material-icons cursor-pointer text-gray-600 hover:text-gray-800" onClick={onCancel}>close</i>
				</div>
				: null
			}
		</div>
	);
}

/** Renders the activity of an issue 
* @function Status
* @param props {Object} The component props
* @param props.issue {Issue} The issue to render
* @returns The issue activity view
* */
const Activity = (props: { issue: Issue }) => {
	const { userId, jwtToken } = useContext(AuthContext);
	const { issue } = props;

	const [comments, setComments] = useState<Comment[]>([]);

	return (
		<div className="col-start-1 col-end-4 grid grid-cols-1 gap-1 pt-5 mt-5 border-t-2 border-gray-600">
			<p className="font-bold text-2xl">Activity</p>
			{comments.map(comment => (
				<div className="m-5">
					<p className="font-bold text-lg">{comment.author}</p>
					<p className="text-md">{comment.comment}</p>
				</div>
			))}
		</div>
	);
}


export default ProjectView;
