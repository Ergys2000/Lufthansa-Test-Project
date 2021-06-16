import React, { useState } from 'react';
import { Link } from 'react-router-dom';

/** Default form */
const Form = (props: any) => {
	return (
		<form
			{...props}
			className={`flex flex-col justify-center items-center ${props.className}`}>
		</form>
	);
}

/** Default label which wraps the input */
const Label = (props: any) => {
	return (
		<label {...props} className={`flex flex-col m-3 text-left w-full ${props.className}`}>
		</label>
	);
}

/** Default input */
const Input = (props: any) => {
	return (
		<input
			{...props}
			className="
				bg-gray-300 appearance-none rounded 
				w-full py-2 px-4 text-gray-700 leading-tight focus:outline-none focus:bg-white 
				focus:border-purple-500 focus:shadow-md"
		/>
	);
}

/** Default select */
const Select = (props: any) => {
	return (
		<select
			{...props}
			className="text-black
				bg-gray-300 rounded 
				w-full py-2 px-4 text-gray-700 leading-tight focus:outline-none focus:bg-white 
				focus:border-purple-500"
		/>
	);
}

/** Default button, can be extended with extra classes
* @function ActionButton
* @param background - sets the value of the background color, one of tailwinds
* options ex: green, red, blue etc.
* @returns A styled button
* */
const ActionButton = (props: any) => {
	let background = "blue";
	if (props.background) background = props.background;
	return (
		<button
			{...props}
			className={`shadow bg-${background}-800 hover:bg-${background}-700 
			focus:shadow-outline focus:outline-none text-white font-bold
			rounded ${props.className}`}>
		</button>
	);
}

/** Default danger button, can be extended with extra classes
* @function DangerButton
* @returns A styled button
* */
const DangerButton = (props: any) => {
	return (
		<button
			{...props}
			className={`shadow bg-red-600 hover:bg-red-500 
			focus:shadow-outline focus:outline-none text-white font-bold
			rounded ${props.className}`}>
		</button>
	);
}

type Location = {
	title: string;
	url: string;
	state?: any;
};
/** The content page context, used to change the header of the page */
const ContentPageContext = React.createContext({
	getLocationList: (): Location[] => [],
	setLocationList: (locationList: Location[]) => console.log(locationList)
});
/** Where content will be displayed in the user page */
const ContentPage = (props: any) => {
	const [locations, setLocations] = useState<Location[]>([]);
	const contextValue = {
		getLocationList: () => [...locations],
		setLocationList: (locationList: Location[]) => setLocations(locationList)
	};
	return (
		<ContentPageContext.Provider value={contextValue}>

			<div className="overflow-auto flex-1 flex flex-col bg-gray-200 min-w-min min-h-min">

				{/* The title list is rendered here */}
				<header className="flex flex-row items-center ml-10">
					{locations.map((location, index) =>
						<Link key={index} 
							to={{pathname: location.url, state: location.state}} 
							className="flex flex-row justify-evenly items-center text-gray-800 text-3xl my-3">
							<p className="hover:text-purple-700">{location.title}</p>
							{/* Render the arrow if it is not the last item in the list */}
							{index !== locations.length - 1
								? <i className="material-icons">arrow_forward</i>
								: null
							}
						</Link>)
					}
				</header>

				{/* The children and other props go here */}
				<section {...props} className="flex-1 text-gray-900"></section>

			</div>

		</ContentPageContext.Provider>
	);
}

/** Navigation container, it can be extended with additional styles
* @function NavBar
* @returns A styled vertical navigation bar
* */
const NavBar = (props: any) => {
	return (
		<nav {...props} className={`group flex flex-col justify-center bg-indigo-900 h-full w-20 hover:w-40 duration-300 ${props.className}`}>
		</nav>
	);
}

/** A pop up window that can be extended with children
* @function Popup
* @returns A popup window which stands on top of everything
* */
const Popup = (props: any) => {
	return (
		<div {...props} className={`overflow-auto inset-0 z-10 fixed w-screen h-screen bg-black bg-opacity-80`}>
		</div>
	);
}

export {
	Form,
	ActionButton,
	DangerButton,
	Input,
	Label,
	Select,
	ContentPage,
	ContentPageContext,
	NavBar,
	Popup,
};
