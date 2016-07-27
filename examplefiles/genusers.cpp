#include <iostream>
#include <stdio.h>
#include <string>
#include <cstdlib>
#include <ctime>
using namespace std;

static const char alphanum[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

int stringLength = sizeof(alphanum) - 1;

char genRandom()  // Random string generator function.
{

    return alphanum[rand() % stringLength];
}

string randomStr(){
	string Str;
	for(unsigned int j = 0; j < 7; ++j){
		Str += genRandom();
	}
	return Str;
}

int main(){
	long base = 120050000;
	long temp;
	for(int i=1;i<=100;i++){
		temp = base+i;
		cout<<temp<<","<<temp<<","<<randomStr()<<"\n";
	}
}