/* The Project type */
export type Project = {
	id: number;
	title: string;
	ownerId: number;
	createdOn: Date;
};

export type Issue = {
	id: number;
	projectId: number,
	title: string;
	description: string;
	status: string;
	reporterId: number;
	reporter: string;
	asigneeId: number;
	asignee: string;
	priority: string;
	createdOn: Date;
	deadline: Date;
};

export type Comment = {
	id: number;
	userId: number;
	author: string;
	comment: string;
};

export type User = {
	id: number;
	firstname: string;
	lastname: string;
	email: string;
	password: string;
	type: string;
	startDate: string;
	supervisor: User | null;
};

export type Request = {
	id: number;
	startDate: string;
	endDate: string;
	createdOn: string;
	approved: boolean | null;
	user: User;
};
