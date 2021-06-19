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
