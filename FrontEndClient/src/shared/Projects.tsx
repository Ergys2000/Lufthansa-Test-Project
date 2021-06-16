import { useEffect, useState, useContext } from 'react';
import { Switch, Route, useHistory, useRouteMatch } from 'react-router-dom';
import { ContentPage, ContentPageContext } from '../styled/Components';
import { Project } from '../types/Common';
import { AuthContext } from '../App';
import ProjectView from './ProjectView';

/** The projects page
* @function Projects 
* @returns The projects page of the user 
* */
const Projects = () => {
	const { path } = useRouteMatch();
	return (
		<ContentPage>
			<Switch>
				<Route path={`${path}/:projectId`}>
					<ProjectView />
				</Route>

				<Route path={`${path}/`}>
					<ProjectList />
				</Route>
			</Switch>
		</ContentPage>
	);
}

/** The project list
* @function ProjectList
* @returns The project list component
* */
const ProjectList = () => {
	const authContext = useContext(AuthContext);
	const { url } = useRouteMatch();

	const contentPageContext = useContext(ContentPageContext);
	useEffect(() => {
		const locationList = [{title: "Projects", url: url}];
		contentPageContext.setLocationList(locationList);
	}, []);

	const [projects, setProjects] = useState<Project[]>([]);

	return (
		<div className="flex flex-row flex-wrap">
			{projects.map(project => <ProjectItem key={project.id} project={project} />)}
		</div>
	);
}

/** Renders a single project in the project list
* @function ProjectItem 
* @param {Object} props - The component props
* @param {Project} props.project - The project the component renders
* @returns A single project in the project list
* */
const ProjectItem = (props: { project: Project }) => {
	const history = useHistory();
	const { url } = useRouteMatch();
	const style = {
		backgroundImage: "url(\"https://trello-backgrounds.s3.amazonaws.com/SharedBackground/480x288/d66341461e7242308038b96e502b60b9/photo-1620207418302-439b387441b0.jpg\")"
	}
	return (
		<div
			onClick={() => history.push(`${url}/${props.project.id}`)}
			style={style}
			className="flex flex-col items-center hover:cursor-pointer w-40 h-20 bg-gray-400 m-5 p-2 rounded-lg bg-cover">
			<p className="text-white">{props.project.title}</p>
		</div>
	);
}

export default Projects;
