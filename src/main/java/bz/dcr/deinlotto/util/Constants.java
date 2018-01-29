package bz.dcr.deinlotto.util;

/**
 * This class contains every constant value, config nodes and permissions
 */
public final class Constants {
	
	private static final String STATE_WARNING = "Utility class!";
	
	public final class Permission {
		
		private Permission () {
			throw new IllegalStateException( STATE_WARNING );
		}
		
		public static final String ADMIN = "permission.admin";
		public static final String TEAM = "permission.team";
		
	}
	
	public final class Plugin {
		
		public final class Prefix {
			
			private Prefix () {
				throw new IllegalStateException( STATE_WARNING );
			}
			
			public static final String CONSOLE = "plugin.prefix.console";
			public static final String INGAME = "plugin.prefix.ingame";
			
		}
		
		public final class TimingInMinutes {
			
			private TimingInMinutes () {
				throw new IllegalStateException( STATE_WARNING );
			}
			
			public static final String ROUNDS = "plugin.timingInMinutes.rounds";
			public static final String BETWEEN_ROUNDS = "plugin.timingInMinutes.betweenRounds";
			
		}
		
		public final class Price {
			
			private Price () {
				throw new IllegalStateException( STATE_WARNING );
			}
			
			public static final String MATERIAL = "plugin.price.material";
			public static final String COUNT = "plugin.price.count";
			
		}
		
		public final class Participations {
			
			private Participations () {
				throw new IllegalStateException( STATE_WARNING );
			}
			
			public static final String COST = "plugin.participations.cost";
			public static final String CURRENCY = "plugin.participations.currency";
			public static final String MINIMUM_PLAYERS_PER_ROUND = "plugin.participations.minimumPlayersPerRound";
			public static final String MAXIMUM_PARTICIPATIONS_PER_PLAYER = "plugin.participations.maximumParticipationsPerPlayer";
			
		}
	}
	
	public final class Message {
		
		public final class Error {
			
			private Error () {
				throw new IllegalStateException( STATE_WARNING );
			}
			
			public static final String GENERAL = "message.error.general";
			public static final String NO_CONSOLE = "message.error.noConsole";
			public static final String NO_PERMISSION = "message.error.noPermission";
			public static final String NO_ECONOMY = "message.error.noEconomy";
			public static final String NO_MONEY = "message.error.noMoney";
			public static final String NO_PARTICIPANTS = "message.error.noParticipents";
			public static final String NO_ACTIVE_ROUND = "message.error.noActiveRound";
			public static final String WRONG_PARAMETER = "message.wrongParameter";
			
		}
		
		public final class Round {
			
			private Round () {
				throw new IllegalStateException( STATE_WARNING );
			}
			
			public static final String START = "message.round.start";
			public static final String END_TEXT = "message.round.end.text";
			public static final String END_COLOR = "message.round.end.color";
			
		}
		
		public final class Participations {
			
			private Participations () {
				throw new IllegalStateException( STATE_WARNING );
			}
			
			public static final String SUCCESS = "message.participations.success";
			public static final String WINNER = "message.participations.winner";
			public static final String REACHED_MAX = "message.participations.reachedMax";
			
		}
		
		public final class Reload {
			
			private Reload () {
				throw new IllegalStateException( STATE_WARNING );
			}
			
			public static final String START = "message.reload.start";
			public static final String END = "message.reload.end";
			
		}
		
		public final class Broadcast {
			
			private Broadcast () {
				throw new IllegalStateException( STATE_WARNING );
			}
			
			public static final String TEXT = "message.broadcast.text";
			public static final String VALUE = "message.broadcast.value";
			
		}
		
		public final class Command {
			
			private Command () {
				throw new IllegalStateException( STATE_WARNING );
			}
			
			public final class Join {
				
				private Join () {
					throw new IllegalStateException( STATE_WARNING );
					
				}
				
				public static final String DESCRIPTION = "message.command.join.description";
				public static final String TEXT = "message.command.join.text";
				
			}
			
			public final class Price {
				
				private Price () {
					throw new IllegalStateException( STATE_WARNING );
					
				}
				
				public static final String DESCRIPTION = "message.command.price.description";
				public static final String TEXT = "message.command.price.text";
				
			}
			
			public final class Headline {
				
				private Headline () {
					throw new IllegalStateException( STATE_WARNING );
					
				}
				
				public static final String SEPERATOR_COLOR = "message.command.headline.seperatorColor";
				public static final String SEPERATOR_SIGN = "message.command.headline.seperatorSign";
				public static final String NAME = "message.command.headline.name";
				
			}
			
			public final class InfoBoard {
				
				private InfoBoard () {
					throw new IllegalStateException( STATE_WARNING );
					
				}
				
				public final class Participants {
					
					private Participants () {
						throw new IllegalStateException( STATE_WARNING );
						
					}
					
					public static final String TEXT = "message.command.infoBoard.participants.text";
					public static final String VALUE = "message.command.infoBoard.participants.value";
					
				}
				
				public final class Tickets {
					
					private Tickets () {
						throw new IllegalStateException( STATE_WARNING );
						
					}
					
					public static final String TEXT = "message.command.infoBoard.tickets.text";
					public static final String VALUE = "message.command.infoBoard.tickets.value";
					
				}
				
				public final class Timeleft {
					
					private Timeleft () {
						throw new IllegalStateException( STATE_WARNING );
						
					}
					
					public static final String TEXT = "message.command.infoBoard.timeleft.text";
					public static final String VALUE = "message.command.infoBoard.timeleft.value";
					
				}
			}
		}
	}
}
