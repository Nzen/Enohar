
Feature: parses lexed eno

Scenario: single line bare name
	Given one line input is "name"
	When I parse input
	Then one line output is _bare name_
























