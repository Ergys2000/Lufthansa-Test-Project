import { ContentPage } from '../styled/Components';

/** @function Home 
* @returns The home page of the user 
* */
const Home = () => {
	return (
		<ContentPage title="Home">
			<PersonalInfo />
		</ContentPage>
	);
}

const PersonalInfo = (props: any) => {
	return (
		<div>
			This is the personal info of the student.
			<select>
				<option></option>
			</select>
		</div>
	);
}

export default Home;
