const Table = (props: any) => {
	return (
		<div {...props} className={`px-10 py-5 mx-10 bg-gray-100 rounded-xl shadow-xl ${props.className}`}>
		</div>
	);
}

const TableHeaderRow = (props: any) => {
	return (
		<div {...props} className={`text-center flex flex-row border-b-2 border-indigo-800 
			justify-center w-full text-indigo-800 p-2 ${props.className}`}>
		</div>
	);
}

const TableRow = (props: any) => {
	return (
		<div {...props} 
			className={`flex flex-row justify-center text-center text-gray-600 w-full p-5 border-b border-gray-400 last:border-0 ${props.className}`}>
		</div>
	);
}

const TableRowData = (props: any) => {
	return (
		<div {...props} className={`flex-1 ${props.className}`}></div>
	);
}

export {
	Table,
	TableHeaderRow,
	TableRow,
	TableRowData,
};
